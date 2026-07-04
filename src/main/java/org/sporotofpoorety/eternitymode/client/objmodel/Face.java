package org.sporotofpoorety.eternitymode.client.objmodel;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;




public class Face 
{

    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;


    @SideOnly(Side.CLIENT)
    public void addFaceForRender(Tessellator2 tessellator) 
    {
        this.addFaceForRender(tessellator, 5.0E-4F);
    }


    @SideOnly(Side.CLIENT)
    public void addFaceForRender(Tessellator2 tessellator, float textureOffset) 
    {
        if (this.faceNormal == null) 
        {
            this.faceNormal = this.calculateFaceNormal();
        }

        tessellator.setNormal(this.faceNormal.x, this.faceNormal.y, this.faceNormal.z);
        float averageU = 0.0F;
        float averageV = 0.0F;

        if (this.textureCoordinates != null && this.textureCoordinates.length > 0) 
        {
            for(int i = 0; i < this.textureCoordinates.length; ++i) 
            {
                averageU += this.textureCoordinates[i].u;
                averageV += this.textureCoordinates[i].v;
            }

            averageU /= (float)this.textureCoordinates.length;
            averageV /= (float)this.textureCoordinates.length;
        }

        for(int i = 0; i < this.vertices.length; ++i) 
        {
            if (this.textureCoordinates != null && this.textureCoordinates.length > 0) 
            {
                float offsetU = textureOffset;
                float offsetV = textureOffset;

                if (this.textureCoordinates[i].u > averageU) 
                {
                    offsetU = -textureOffset;
                }

                if (this.textureCoordinates[i].v > averageV) 
                {
                    offsetV = -textureOffset;
                }

                tessellator.addVertexWithUV((double)this.vertices[i].x, (double)this.vertices[i].y, (double)this.vertices[i].z, (double)(this.textureCoordinates[i].u + offsetU), (double)(this.textureCoordinates[i].v + offsetV));
            } 
            else 
            {
                tessellator.addVertex((double)this.vertices[i].x, (double)this.vertices[i].y, (double)this.vertices[i].z);
            }
        }
    }


    public Vertex calculateFaceNormal() 
    {
        Vec3 v1 = Vec3.createVectorHelper((double)(this.vertices[1].x - this.vertices[0].x), (double)(this.vertices[1].y - this.vertices[0].y), (double)(this.vertices[1].z - this.vertices[0].z));
        Vec3 v2 = Vec3.createVectorHelper((double)(this.vertices[2].x - this.vertices[0].x), (double)(this.vertices[2].y - this.vertices[0].y), (double)(this.vertices[2].z - this.vertices[0].z));
        Vec3 normalVector = null;
        normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float)normalVector.xCoord, (float)normalVector.yCoord, (float)normalVector.zCoord);
    }

}
