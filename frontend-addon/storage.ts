function getPresetsFromStorage() : PresetDetails[]
{
    console.log("getPresetsFromStorage")
    let presets = GetProperty<PresetDetails[]>(presetString);
    if(!presets || presets.length === 0 || theOnlyPresetIsTheDefaultOne(presets))
    {
        const returned = getPresets();
        console.log(returned)
        if(returned === "404")
        {
            presets = Array.of(defaultPreset)
        }
        else
        {
            presets = returned;
            presets.unshift(defaultPreset);
        }
        SetProperty(presetString, presets);
    }
    console.log({presets})
    return presets;
}

function theOnlyPresetIsTheDefaultOne(presets : PresetDetails[]) : boolean
{
    return presets.length === 1 && JSON.stringify(defaultPreset) === JSON.stringify(presets[0]);
}

function getCurrentPresetFromStorage()
{
    console.log("getCurrentPresetFromStorage")
    const index = GetProperty<number>(currentPresetIndexString);
    console.log({index})
    return getPresetsFromStorage()[index];
}