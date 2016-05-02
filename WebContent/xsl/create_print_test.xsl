<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:variable name="survey_title" />
    <xsl:template match="/questestinterop/assessment">
        
        <div class="wrapper">

            <div class="test">
            
                <div class="title"><xsl:value-of select="@title"/></div>

                <xsl:for-each select="section">
                    <div class="section">
                        <xsl:for-each select="item">
                            <div class="question_block">
                                <div class="question">
                                    <span class="question_number"><xsl:value-of select="position()"/>. </span>
                                    <span class="question_text"><xsl:value-of select="presentation/flow/flow/flow/material/mat_extension/mat_formattedtext" disable-output-escaping="yes"/></span>
                                </div>
                                <div class="answer">
                                    <xsl:choose>
                                        <xsl:when test="itemmetadata/bbmd_questiontype='Short Response'">
                                            <span class="shortanswer_lines">_____________________________________________________________________________________________</span><br/>
                                            <span class="shortanswer_lines">_____________________________________________________________________________________________</span><br/>
                                            <span class="shortanswer_lines">_____________________________________________________________________________________________</span><br/>
                                        </xsl:when>
                                        <xsl:when test="itemmetadata/bbmd_questiontype='True/False'">
                                            <div class="tf_answer">
                                                <div class="checkbox">&#160;</div> 
                                                <span class="option_text">True</span>
                                                <div style="clear:both;">&#160;</div>
                                            </div>
                                            <div class="tf_answer">
                                                <div class="checkbox">&#160;</div> 
                                                <span class="option_text">False</span>
                                                <div style="clear:both;">&#160;</div>
                                            </div>
                                        </xsl:when>
                                        <xsl:when test="itemmetadata/bbmd_questiontype='Multiple Choice'">
                                            <div class="mc_answer">
                                                <xsl:for-each select="presentation/flow/flow/response_lid/render_choice/flow_label">
                                                    <div class="checkbox">&#160;</div>
                                                    <span class="option_text"><xsl:value-of select="response_label/flow_mat/material/mat_extension/mat_formattedtext" disable-output-escaping="yes"/></span>
                                                    <div style="clear:both;">&#160;</div>
                                                </xsl:for-each>
                                            </div>
                                        </xsl:when>
                                    </xsl:choose>
                                </div>
                            </div>
                        </xsl:for-each>
                    </div>
                </xsl:for-each>

            </div>
        </div>
        
    </xsl:template>
    
    <xsl:template match="text()"/> <!-- to catch all unmatched text -->
    
</xsl:stylesheet> 
