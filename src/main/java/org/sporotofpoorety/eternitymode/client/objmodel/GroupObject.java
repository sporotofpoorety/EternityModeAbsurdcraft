package org.sporotofpoorety.eternitymode.client.objmodel;




import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;




public class GroupObject 
{

    public String name;
    public ArrayList<Face> faces;
    public int glDrawingMode;


    public GroupObject() 
    {
        this("");
    }

    public GroupObject(String name) 
    {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode) 
    {
        this.faces = new ArrayList();
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }




    @SideOnly(Side.CLIENT)
    public void render() 
    {
        if (this.faces.size() > 0) 
        {
            Tessellator2 tessellator = Tessellator2.instance;
            tessellator.startDrawing(this.glDrawingMode);
            this.render(tessellator);
            tessellator.draw();
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(Tessellator2 tessellator) 
    {
        if (this.faces.size() > 0) 
        {
            for(Face face : this.faces) 
            {
                face.addFaceForRender(tessellator);
            }
        }
    }
}
