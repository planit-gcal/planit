function getPresetsFromStorage() : PresetDetails[]
{
    console.log("getPresetsFromStorage")
    let presets = PropertyManager.getProperty<PresetDetails[]>(presetString);
    if(!presets || presets.length === 0 || theOnlyPresetIsTheDefaultOne(presets))
    {
        const returned = API.getPresets();
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
        PropertyManager.setProperty(presetString, presets);
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
    const index = PropertyManager.getProperty<number>(currentPresetIndexString);
    console.log({index})
    return getPresetsFromStorage()[index];
}

function updateGuests(presets: PresetDetails[], currentIndex: number) {
    console.log({presets, currentIndex});
    const preset = presets[currentIndex];
    const guests = preset.guests;
    PropertyManager.setProperty(usersString, guests);
}