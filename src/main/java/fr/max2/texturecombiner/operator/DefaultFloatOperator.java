package fr.max2.texturecombiner.operator;


public enum DefaultFloatOperator implements IFloatOperator
{
	FIRST
	{
		@Override
		public float apply(float first, float second)
		{
			return first;
		}
	},
	SECOND
	{
		@Override
		public float apply(float first, float second)
		{
			return second;
		};
	},
	PRODUCT
	{
		@Override
		public float apply(float first, float second)
		{
			return first * second;
		}
	},
	ONE_MINUS_FIRST
	{
		@Override
		public float apply(float first, float second)
		{
			return 1 - first;
		}
	},
	ONE_MINUS_SECOND
	{
		@Override
		public float apply(float first, float second)
		{
			return 1 - second;
		}
	},
	ONE_MINUS_PRODUCT
	{
		@Override
		public float apply(float first, float second)
		{
			return 1 - (first * second);
		}
	},
	OPPOSITE_PRODUCT
	{
		@Override
		public float apply(float first, float second)
		{
			return 1 - ((1 - first) * (1 - second));
		}
	},
	SHIFT
	{
		@Override
		public float apply(float first, float second)
		{
			return first + second;
		}
	};
	
	
	public static DefaultFloatOperator getOperator(String operatorName)
	{
		switch (operatorName.toLowerCase())
		{
		case "f":
		case "first":
			return FIRST;
		case "s":
		case "second":
			return SECOND;
		case "*":
		case "mult":
		case "multiply":
		case "product":
			return PRODUCT;
		case "1-f":
		case "one_minus_first":
		case "one minus first":
			return ONE_MINUS_FIRST;
		case "1-s":
		case "one_minus_second":
		case "one minus second":
			return ONE_MINUS_SECOND;
		case "1-*":
		case "one_minus_multiply":
		case "one_minus_product":
		case "one minus multiply":
		case "one minus product":
			return ONE_MINUS_PRODUCT;
		case "-*":
		case "oposite_multiply":
		case "oposite_product":
		case "oposite multiply":
		case "oposite product":
			return OPPOSITE_PRODUCT;
		
		default:
			throw new IllegalArgumentException("Invalid float operattion: " + operatorName);
		}
	}
}
