/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.cms;

import blackboard.cms.filesystem.CSContext;
import blackboard.cms.filesystem.CSDirectory;
import blackboard.cms.filesystem.CSEntry;
import blackboard.cms.filesystem.CSFile;
import blackboard.cms.metadata.MetadataManager;
import blackboard.cms.metadata.MetadataManagerFactory;
import blackboard.cms.metadata.XythosMetadata;
import blackboard.cms.xythos.XythosContextUtil;
import blackboard.cms.xythos.impl.BlackboardFileMetaData;
import blackboard.persist.PersistenceException;
import blackboard.platform.contentsystem.data.Resource;
import blackboard.platform.contentsystem.manager.DocumentManager;
import blackboard.platform.contentsystem.service.ContentSystemService;
import blackboard.platform.contentsystem.service.ContentSystemServiceFactory;
import com.xythos.common.api.VirtualServer;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.storageServer.api.FileSystem;
import com.xythos.storageServer.api.FileSystemEntry;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author peter
 */
public class CMSReadWrite {
    public static void writeFile( String basePath, String filename, String fileContents ) {
        CSContext cs_context = null;
        ByteArrayInputStream file_to_write_is = null;

        try {
            cs_context = CSContext.getContext();
            
            file_to_write_is = new ByteArrayInputStream( fileContents.getBytes() );

            cs_context.isSuperUser( true );
            CSFile newFile = cs_context.createFile( basePath, filename, file_to_write_is, true ); // do we want to overwrite or not?
//            FileSystemEntry new_fse = new_file.getFileSystemEntry();
//            new_fse.setOwner(applicant_username);
            
            System.out.println("Wrote file " + filename + " to the folder " + basePath);

        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            String stackTrace = "";
            e.printStackTrace(new PrintWriter(stringWriter));
            stackTrace = stringWriter.toString();
            System.out.println(stackTrace);
            System.out.println("Error writing LDC file " + filename + " to " + basePath + ": " + e.getMessage());
            cs_context.rollback();
        } finally {
            if (cs_context != null) {
                try {
					cs_context.commit();
				} catch (XythosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                try {
                    file_to_write_is.close();
                } catch (IOException e_io) {
                    System.out.println("IO Exception - can't close InputStream!");
                }
            }
        }   
    
    }
    
    public static String readMetadata( String filepath, String metadataField )
    {
        com.xythos.security.api.Context q_context = null;
        String bb_namespace = "blackboard.com";
        
        if( ( metadataField==null )||( metadataField.equals("") ) ) {
			return "";
		}
		
        try {
            q_context = XythosContextUtil.getContext();
            VirtualServer virtual_Server = VirtualServer.getDefaultVirtualServer();
			
            FileSystemEntry fsEntry = FileSystem.findEntry( virtual_Server, filepath, false, q_context );
            BlackboardFileMetaData bbmetadata = new BlackboardFileMetaData( fsEntry );
			
            return bbmetadata.getCustomProperty( bb_namespace, metadataField ).replace( "::", "" );
			
        } catch (Exception e) {
            System.out.println("Error reading metadata: " + e.getMessage());
            StringWriter stringWriter = new StringWriter();
            String stackTrace = "";
            e.printStackTrace(new PrintWriter(stringWriter));
            stackTrace = stringWriter.toString();
            System.out.println(stackTrace.replace("\n", "<br/>"));
            XythosContextUtil.rollbackContext( q_context );
        } finally {
             XythosContextUtil.releaseContext( q_context );
        }
        
        return "";
    }
    
    public static void writeMetadata( String filepath, String metadataField, String newValue )
    {
        com.xythos.security.api.Context p_context = null;
        String bb_namespace = "blackboard.com";

        try {

            p_context = XythosContextUtil.getContext();
            VirtualServer virtualServer = VirtualServer.getDefaultVirtualServer();
            FileSystemEntry fsEntry = FileSystem.findEntry( virtualServer, filepath, false, p_context );
            
            BlackboardFileMetaData bbmetadata = new BlackboardFileMetaData( fsEntry );
            bbmetadata.setCustomProperty( bb_namespace, metadataField, newValue );
			
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            String stackTrace = "";
            e.printStackTrace(new PrintWriter(stringWriter));
            stackTrace = stringWriter.toString();
            System.out.println(stackTrace);
            System.out.println("Xythos/metadata exception");
            XythosContextUtil.rollbackContext((com.xythos.security.api.Context) p_context);
        } finally {
            XythosContextUtil.releaseContext((com.xythos.security.api.Context) p_context);
        }   
    }
    
    
    public static void saveUploadedFile( InputStream fileContents, String filename, String basePath, String newDirectoryName, Properties metadataProperties ) {
        if( (!filename.isEmpty() )&&( !basePath.isEmpty() )) {
            CSContext csContext = null;
            
            try {
                csContext = CSContext.getContext();

                String fullPath = basePath + "/" + newDirectoryName;
                if( csContext.findEntry( fullPath )==null ) {
                    csContext.createDirectory( basePath, newDirectoryName );
                }
                
                CSFile file_handle = csContext.createFile( fullPath, filename, fileContents, true );
                FileSystemEntry fileHandle = file_handle.getFileSystemEntry();
				if( metadataProperties!=null ) {
					CMSReadWrite.setUploadedFileMetadata( fileHandle, metadataProperties );
				}

            } catch (Exception e) {
                StringWriter stringWriter = new StringWriter();
                String stackTrace = "";
                e.printStackTrace(new PrintWriter(stringWriter));
                stackTrace = stringWriter.toString();
                System.out.println( stackTrace );

                csContext.rollback();
            } finally {
                if( csContext != null ) {
                    try {
						csContext.commit();
					} catch (XythosException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }
    }    
    public static void saveUploadedFileOld(InputStream filecontents, String filename, String base_filepath, String directory_name, Properties metadata_properties)
    {
        System.out.println("In saveUploadedFile");
        if ((!filename.isEmpty()) && (!base_filepath.isEmpty()))
        {
            // write the file to the CMS  
            CSContext cs_context = null;
            String full_path = "<none>";
            
            System.out.println("filename: " + filename);
            
            try {
                cs_context = CSContext.getContext();

                full_path = base_filepath + "/" + directory_name;
                if (cs_context.findEntry(full_path) == null)
                {
                    cs_context.createDirectory(base_filepath, directory_name);
                }
                
                CSFile file_handle = cs_context.createFile(full_path, filename, filecontents, true);
                FileSystemEntry fse_file_handle = file_handle.getFileSystemEntry();                
                CMSReadWrite.setUploadedFileMetadata(fse_file_handle, metadata_properties);

            } catch (Exception e) {
                StringWriter stringWriter = new StringWriter();
                String stackTrace = "";
                e.printStackTrace(new PrintWriter(stringWriter));
                stackTrace = stringWriter.toString();
                System.out.println(stackTrace);

                cs_context.rollback();
            } finally {
                if (cs_context != null) {
                    System.out.println("Committing the file " + full_path + "...");
                    try {
						cs_context.commit();
					} catch (XythosException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }
    }    
    
    private static void setUploadedFileMetadata( FileSystemEntry fileHandle, Properties metadataProperties ) 
			throws XythosException {
		
        com.xythos.security.api.Context x_context = XythosContextUtil.getContext();
        try {
            MetadataManager mdm = MetadataManagerFactory.getInstance();
            XythosMetadata newMetadata = mdm.convertFromProperties( metadataProperties );
            mdm.save( newMetadata, fileHandle );
        }
        catch(Exception e) {
            StringWriter stringWriter = new StringWriter();
            String stackTrace = "";
            e.printStackTrace(new PrintWriter(stringWriter));
            stackTrace = stringWriter.toString();
            System.out.println(stackTrace);
            XythosContextUtil.rollbackContext(x_context);
        } finally {
            XythosContextUtil.releaseContext(x_context);
        }
    }
    
	public static String getFullPathFromXID( String xid ) {
		String fullPath = null;
		
		xid = "xid-" + xid.toLowerCase().replaceAll("xid-", "");
		String resourceLocation = ContentSystemService.WEBDAV_SERVLET_PATH + File.separator + xid;
		try {
			ContentSystemService contentSystemService = ContentSystemServiceFactory.getInstance();
			DocumentManager documentManager = contentSystemService.getDocumentManager();
			Resource r = documentManager.loadResource( resourceLocation );
			fullPath = r.getLocation();
		} catch (PersistenceException e) {
		
		}
		
		return fullPath;
	}
	
	
    public static HashMap< String,String > getDirectoryContents( String directoryPath )
    {
        HashMap< String,String > files = new HashMap< String,String >();
		CSContext cs = CSContext.getContext();
		CSDirectory dir = ( CSDirectory )cs.findEntry( directoryPath );

		try {
			if( dir!=null ) {
				/*  GET ALL ENTRIES IN DIRECTORY
				 * ===========================================
				 */
				List<CSEntry> dirContents = dir.getDirectoryContents();

				for( CSEntry thisEntry : dirContents ) {

					//FileSystemFile thisFile = ( FileSystemFile )thisEntry;
					FileSystemEntry thisFile = ( FileSystemEntry )thisEntry.getFileSystemEntry();

					String thisName = thisFile.getName().substring( thisFile.getName().lastIndexOf('/') + 1 );
					String thisURL = "/bbcswebdav/xid-" + thisFile.getEntryID();
					/*System.out.print( "file: " + thisName );
					System.out.print( ", url: " + thisURL );
					System.out.print( ", purpose (metadata): " + CMSReadWrite.readMetadata( directoryPath, "ims_adv_classification_purpose" ) );
					System.out.print( "\n" );*/

					files.put( thisName, thisURL );
				}
			}
		}
		catch( Exception e ) {
		   StringWriter stringWriter = new StringWriter();
		   e.printStackTrace( new PrintWriter( stringWriter ) );
		   String stackTrace = stringWriter.toString();
		   System.out.println( stackTrace );
	   } finally {
	   }
            
        return files;
    }
}
