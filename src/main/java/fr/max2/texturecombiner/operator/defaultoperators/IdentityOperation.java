package fr.max2.texturecombiner.operator.defaultoperators;

import fr.max2.texturecombiner.operator.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;

public enum IdentityOperation implements ITextureOperator
{
	INSTANCE
	{
		@Override
		public NativeImage apply(NativeImage[] inputs)
		{
			NativeImage in = inputs[0];
			NativeImage copy = new NativeImage(in.getFormat(), in.getWidth(), in.getHeight(), false);
			copy.copyImageData(in);
			return copy;
		}
	};
}
