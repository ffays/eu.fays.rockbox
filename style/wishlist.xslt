<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <html>
  <head>
  	<title>Wish list</title>
  	<link rel="stylesheet" type="text/css" href="style/John_Sardine__Simple_Little_Table_CSS3.css" />
  </head>
  <body>
    <table>
      <tr>
        <th>SKU</th>
        <th>Available</th>
        <th>Price</th>
        <th>Image</th>
        <th>Description</th>
      </tr>
      <xsl:for-each select="wishList/article">
        <tr>
          <td><a href="http://dx.com/p/{@sku}"><xsl:value-of select="@sku"/></a></td>
          <td><xsl:value-of select="@available"/></td>
          <td><xsl:value-of select="@price"/></td>
          <td><img src="http://img.dxcdn.com/productimages/sku_{@sku}_1_small.jpg" /></td>
          <td><xsl:value-of select="text()"/></td>
        </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>