<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tjs="http://www.opengis.net/tjs/1.0" version="1.0">
    <xsl:output method='text' version='1.0' encoding='UTF-8' indent='no'/>    
    <xsl:template match="/">key;title<xsl:for-each select="//tjs:Row"><xsl:text>
</xsl:text><xsl:value-of select="./tjs:K"/>;<xsl:value-of select="./tjs:Title"/>
</xsl:for-each></xsl:template>
</xsl:stylesheet>