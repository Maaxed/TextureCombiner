# TextureCombiner

This Minecraft Forge mod allows you to automatically generate textures from existing textures based on JSON files.

## Generating a texture

To generate a texture named `example_mod:block/example` you need to create the file `example.png.json` in the folder where the texture would normally be (in this example, `assets/example_mod/textures/block/`)

### JSON Syntax

```json
{
	"operator": "operator_mod:operator_id",
	"inputs": [
		"mod_id:texture/path",
		"other:texture"
	],
	"options": {
		"some_option": "some_value",
		"another_option": "another_value"
	}
}
```

Attributes:

- `"operator"`: the registry name of the [operator](#default-operators) that is going to be applied
- `"inputs"`: the list of textures that are going to be processed by the operator (More info in the section [Combining operations](#combining-operations))
- `"options"`: the options to configure the operation (optional)

### Default Operators

- `texturecombiner:layers`:  
	Place multiple textures on top of each other.  
	The inputs are ordered from the back to the front.  
	Options:
	- `"overwrite"`: true / false (default: false)  
		When set to false, the pixels of all layers will be blended together  
		When set to true, semi-transparent pixels will overwrite pixels from lower layers
- `texturecombiner:grayscale`:  
	Creates a grayscale copy of the input texture.  
	Exacly one input should be supplied.  
	No options available
- `texturecombiner:rotate`:  
	Creates a rotated copy of the input texture.  
	Exacly one input should be supplied.  
	Options:
	- `"rotation"`: -90 / 0 / 90 / 180 (mandatory)  
		The desired rotation angle (clockwise)
- `texturecombiner:mirror`:  
	Creates a mirrored copy of the input texture.  
	Exacly one input should be supplied.  
	Options:
	- `"mirror"`: "horizontal" / "vertical" (mandatory)  
		When set to "horizontal", left and right will be flipped  
		When set to "vertical", top and bottom will be flipped

### Combining operations

You can combine multiple operations in one JSON file by placing a JSON object in the inputs of your operation. The JSON object should contain the [attributes describe above](#json-syntax).

Example:

```json
{
	"operator": "texturecombiner:layers",
	"inputs": [
		"minecraft:block/stone",
		{
			"operator": "texturecombiner:grayscale",
			"inputs": "minecraft:item/apple"
		}
	]
}
```