package fr.max2.texturecombiner.operator;

import net.minecraft.client.renderer.texture.NativeImage;

public interface IInterpolation
{
	int getPixel(NativeImage img, int u, int v, int w, int h);
}
