package fr.max2.texturecombiner.defaultoperators;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import fr.max2.texturecombiner.ITextureOperator;
import net.minecraft.client.renderer.texture.NativeImage;

public enum IdentityOperation implements ITextureOperator
{
	INSTANCE
	{
		@Override
		public NativeImage apply(NativeImage[] inputs)
		{
			try
			{
				return NativeImage.read(inputs[0].getFormat(), ByteBuffer.wrap(inputs[0].getBytes()));
			}
			catch (IOException e)
			{
				throw new UncheckedIOException("Could not copy texture : ", e);
			}
		}
	};
}
