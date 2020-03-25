package fr.max2.texturecombiner.defaultoperators;

import fr.max2.texturecombiner.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;

public class ResizeOperation implements ITextureOperator
{
	private final int newWidth, newHeight;
	private final InterpolationType interpolation;
	
	public ResizeOperation(int newWidth, int newHeight, InterpolationType interpolation)
	{
		this.newWidth = newWidth;
		this.newHeight = newHeight;
		this.interpolation = interpolation;
	}

	@Override
	public NativeImage apply(NativeImage[] inputs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static enum InterpolationType
	{
		;
	}
}
