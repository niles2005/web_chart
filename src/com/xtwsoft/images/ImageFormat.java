/**
 * 
 *
 * History:
 *   Jun 1, 2007 1:00:00 PM Created by NieLei
 */
package com.xtwsoft.images;


/**
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version 1.0 CreateTime:Jun 1, 2007 1:00:00 PM
 *
 */
public class ImageFormat {
    public static final int UnknownImageType = 0;
    public static final int PNG = 1;
    public static final int GIF = 2;
    private int m_colors = 8;//defalut 8    (8,24,32)
    private int m_compression = 6; //(0-9)  0:non compress, 9:compress largest
    private int m_type = PNG;

    public static final String IMG_TYPE_PNG = "PNG";
    public static final String IMG_TYPE_JPG = "JPG";
    public static final String IMG_TYPE_GIF = "GIF";
    
    public ImageFormat() {
    }

    public void setFormat(String strType) {
        if (strType == null) {
            return;
        }

        strType = strType.trim().toUpperCase();
        if (IMG_TYPE_PNG.equals(strType)) {
            m_type = PNG;
        }
        else if (IMG_TYPE_GIF.equals(strType)) {
            m_type = GIF;
        }
    }

    public void setType(int type, int colors, int compression) {
        m_type = type;
        setColors(colors);
        m_compression = compression;
    }

    public void setImageFormat(ImageFormat imageFormat) {
        if (imageFormat != null) {
            m_type = imageFormat.m_type;
            m_colors = imageFormat.m_colors;
            m_compression = imageFormat.m_compression;
        }
    }

    public void setType(int type, int colors) {
        m_type = type;
        setColors(colors);
    }

    public void setType(int type) {
        m_type = type;
    }

    public int getType() {
        return m_type;
    }

    public void setColors(String strColors) {
        if (strColors == null) {
            return;
        }
        try {
            setColors(Integer.parseInt(strColors.trim()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setColors(int colors) {
        m_colors = colors;
    }

    public void setCompression(String strCompression) {
        if (strCompression == null) {
            return;
        }
        try {
            setCompression(Integer.parseInt(strCompression.trim()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCompression(int compression) {
        m_compression = compression;
    }

    public void setQuality(String strQuality) {
        if (strQuality == null) {
            return;
        }
        try {
            setQuality(Integer.parseInt(strQuality.trim()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setQuality(int quality) {
        //todo for GIF's parameter
    }

    public int getColors() {
        return m_colors;
    }

    public int getCompression() {
        return m_compression;
    }
}
