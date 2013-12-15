<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output
	method="text"
	indent="no"
	encoding="UTF-8"
	doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
	/>


<xsl:template match="credits">
	<xsl:apply-templates select="section"/>
</xsl:template>

<xsl:template match="section">
	<xsl:value-of select="@name"/>
	<xsl:text>;</xsl:text>
	<xsl:apply-templates select="member"/>
	<xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="member">
	<xsl:value-of select="."/>
	<xsl:text>;</xsl:text>
</xsl:template>

</xsl:stylesheet>