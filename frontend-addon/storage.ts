function getPresetsFromStorage() : PresetDetails[]
{
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
    return presets;
}

function theOnlyPresetIsTheDefaultOne(presets : PresetDetails[]) : boolean
{
    return presets.length === 1 && JSON.stringify(defaultPreset) === JSON.stringify(presets[0]);
}

function getCurrentPresetFromStorage()
{
    const index = GetProperty<number>(currentPresetIndexString);
    return getPresetsFromStorage()[index];
}