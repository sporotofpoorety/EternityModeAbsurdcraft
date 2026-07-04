package org.sporotofpoorety.eternitymode.client.objmodel;


import net.minecraft.util.math.MathHelper;




public class Vec3
{

    public double xCoord;
    public double yCoord;
    public double zCoord;




    public static Vec3 createVectorHelper(double p_72443_0_, double p_72443_2_, double p_72443_4_) 
    {
       return new Vec3(p_72443_0_, p_72443_2_, p_72443_4_);
    }


    protected Vec3(double p_i1108_1_, double p_i1108_3_, double p_i1108_5_) 
    {
        if (p_i1108_1_ == (double)-0.0F) 
        {
            p_i1108_1_ = (double)0.0F;
        }

        if (p_i1108_3_ == (double)-0.0F) 
        {
            p_i1108_3_ = (double)0.0F;
        }

        if (p_i1108_5_ == (double)-0.0F)
        {
            p_i1108_5_ = (double)0.0F;
        }

        this.xCoord = p_i1108_1_;
        this.yCoord = p_i1108_3_;
        this.zCoord = p_i1108_5_;
    }


    protected Vec3 setComponents(double p_72439_1_, double p_72439_3_, double p_72439_5_)
    {
        this.xCoord = p_72439_1_;
        this.yCoord = p_72439_3_;
        this.zCoord = p_72439_5_;
        return this;
    }


    public Vec3 subtract(Vec3 vec) 
    {
        return createVectorHelper(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
    }


    public Vec3 normalize() 
    {
        double d0 = (double)MathHelper.sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);

        return d0 < 1.0E-4 ? createVectorHelper((double)0.0F, (double)0.0F, (double)0.0F) : createVectorHelper(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
    }


    public double dotProduct(Vec3 vec) 
    {
        return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
    }


    public Vec3 crossProduct(Vec3 vec) 
    {
        return createVectorHelper(this.yCoord * vec.zCoord - this.zCoord * vec.yCoord, this.zCoord * vec.xCoord - this.xCoord * vec.zCoord, this.xCoord * vec.yCoord - this.yCoord * vec.xCoord);
    }


    public Vec3 addVector(double x, double y, double z) 
    {
        return createVectorHelper(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }


    public double distanceTo(Vec3 vec) 
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }


    public double squareDistanceTo(Vec3 vec) 
    {
        double d0 = vec.xCoord - this.xCoord;
        double d1 = vec.yCoord - this.yCoord;
        double d2 = vec.zCoord - this.zCoord;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }


    public double squareDistanceTo(double p_72445_1_, double p_72445_3_, double p_72445_5_) 
    {
        double d3 = p_72445_1_ - this.xCoord;
        double d4 = p_72445_3_ - this.yCoord;
        double d5 = p_72445_5_ - this.zCoord;
        return d3 * d3 + d4 * d4 + d5 * d5;
    }


    public double lengthVector() 
    {
        return (double)MathHelper.sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }


    public Vec3 getIntermediateWithXValue(Vec3 vec, double x) 
    {
        double d1 = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;

        if (d1 * d1 < (double)1.0E-7F) 
        {
            return null;
        } 
        else 
        {
            double d4 = (x - this.xCoord) / d1;
            return d4 >= (double)0.0F && d4 <= (double)1.0F ? createVectorHelper(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }


    public Vec3 getIntermediateWithYValue(Vec3 vec, double y) 
    {
        double d1 = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;

        if (d2 * d2 < (double)1.0E-7F) 
        {
            return null;
        } 
        else 
        {
            double d4 = (y - this.yCoord) / d2;
            return d4 >= (double)0.0F && d4 <= (double)1.0F ? createVectorHelper(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }


    public Vec3 getIntermediateWithZValue(Vec3 vec, double z) 
    {
        double d1 = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;

        if (d3 * d3 < (double)1.0E-7F) 
        {
            return null;
        } 
        else 
        {
            double d4 = (z - this.zCoord) / d3;
            return d4 >= (double)0.0F && d4 <= (double)1.0F ? createVectorHelper(this.xCoord + d1 * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
        }
    }


    public String toString() 
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }


    public void rotateAroundX(float p_72440_1_) 
    {
        float f1 = MathHelper.cos(p_72440_1_);
        float f2 = MathHelper.sin(p_72440_1_);
        double d0 = this.xCoord;
        double d1 = this.yCoord * (double)f1 + this.zCoord * (double)f2;
        double d2 = this.zCoord * (double)f1 - this.yCoord * (double)f2;
        this.setComponents(d0, d1, d2);
    }


    public void rotateAroundY(float p_72442_1_) 
    {
        float f1 = MathHelper.cos(p_72442_1_);
        float f2 = MathHelper.sin(p_72442_1_);
        double d0 = this.xCoord * (double)f1 + this.zCoord * (double)f2;
        double d1 = this.yCoord;
        double d2 = this.zCoord * (double)f1 - this.xCoord * (double)f2;
        this.setComponents(d0, d1, d2);
    }


    public void rotateAroundZ(float p_72446_1_) 
    {
        float f1 = MathHelper.cos(p_72446_1_);
        float f2 = MathHelper.sin(p_72446_1_);
        double d0 = this.xCoord * (double)f1 + this.yCoord * (double)f2;
        double d1 = this.yCoord * (double)f1 - this.xCoord * (double)f2;
        double d2 = this.zCoord;
        this.setComponents(d0, d1, d2);
    }

}
