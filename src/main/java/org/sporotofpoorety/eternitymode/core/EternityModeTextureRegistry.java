package org.sporotofpoorety.eternitymode.core;


import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;


import org.sporotofpoorety.eternitymode.Tags;




@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public class EternityModeTextureRegistry
{

    public static TextureAtlasSprite[] BLASTWAVE_SPRITES;
    public static TextureAtlasSprite BUBBLE_SPRITE;
    public static TextureAtlasSprite BLANK_SPRITE;
    public static TextureAtlasSprite[] FIREBALL_SPRITES;
    public static TextureAtlasSprite[] SHOCKWAVE_SPRITES;
    public static TextureAtlasSprite[] SMOKE_SPRITES;
    public static TextureAtlasSprite[] SPARKS_SPRITES;
    public static TextureAtlasSprite[] UNDERWATER_BLASTWAVE_SPRITES;


//Take a texture map,
//a resourcelocation name, and frame count
    private static TextureAtlasSprite[] registerSprites(TextureMap map, String baseName, int frameCount) 
    {
        TextureAtlasSprite[] sprites = new TextureAtlasSprite[frameCount];

//Then fill that texture atlas with resource locations
        for (int i = 0; i < frameCount; i++) 
        {
            ResourceLocation location = new ResourceLocation(Tags.MOD_ID, "particle/" + baseName + (i + 1));

            sprites[i] = map.registerSprite(location);
        }

        return sprites;
    }


    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) 
    {
        TextureMap map = event.getMap();

        BLANK_SPRITE = map.registerSprite(new ResourceLocation(Tags.MOD_ID, "particle/blank"));
        BUBBLE_SPRITE = map.registerSprite(new ResourceLocation(Tags.MOD_ID, "particle/explosion/bubble"));

        BLASTWAVE_SPRITES = registerSprites(map, "explosion/blastwave", 21);
        FIREBALL_SPRITES = registerSprites(map, "explosion/fireball", 9);
        SHOCKWAVE_SPRITES = registerSprites(map, "explosion/shockwave", 16);
        SMOKE_SPRITES = registerSprites(map, "explosion/smoke", 12);
        SPARKS_SPRITES = registerSprites(map, "explosion/spark", 4);
        UNDERWATER_BLASTWAVE_SPRITES = registerSprites(map, "explosion/underwaterblastwave", 21);
    }

}
