<?xml version="1.0" encoding="UTF-8"?>

<!--
 Document   : curriculum_manifest_menu.xsl
 Created on : November 19, 2015, 4:28 PM
 Author     : peter
 Description:
  Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>

	<!-- TODO customize transformation rules 
   syntax recommendation http://www.w3.org/TR/xslt 
 -->
	<xsl:template match="/">
		<html>
			<head>
				<title>Preload Dashboard</title>
				<link rel="stylesheet" type="text/css" href="https://www.bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/preload_dashboard_styles.css" />

				<script src="javascript/jquery-latest.min.js"></script>
				<script src="https://www.bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/preload_dashboard_scripts.js"></script>
			</head>
			<body>
				<form action="GeneratePreload" method="POST">
					<xsl:variable name="separator">
						<xsl:text>--</xsl:text>
					</xsl:variable>
					<xsl:for-each select="subjects/subject">
						<xsl:variable name="subject_name" select="@name" />
						<div class="subjlevel_entry">
							<div class="subject_name"><xsl:value-of select="@name" /></div>
							<div class="level_list">
								<xsl:for-each select="levels/level">
									<xsl:variable name="level_name" select="@id" />
									<xsl:variable name="subjlevel_code">
										<xsl:value-of select="$subject_name" /><xsl:value-of select="$separator" /><xsl:value-of select="$level_name" />
									</xsl:variable>
									<div class="level">
										<input type="radio" name="search_code" >
											<xsl:attribute name="value">
											   <xsl:value-of select="$subjlevel_code" />
										   </xsl:attribute>
										</input>
										<xsl:value-of select="curric_name" /> (
										<xsl:value-of select="@id" />: 
										<xsl:value-of select="res_class_id" />)
									</div>
								</xsl:for-each>
							</div>
						</div>
					</xsl:for-each>
					<input type="hidden" name="separator">
						<xsl:attribute name="value">
						   <xsl:value-of select="$separator" />
					   </xsl:attribute>
					</input>
					<input type="submit" value="PRELOAD" />
				</form>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
