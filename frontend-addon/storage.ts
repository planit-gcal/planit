function getPresetsFromStorage()
{
    let presets = GetProperty<Preset[]>("Presets");
    if(!presets || presets.length === 0)
    {
        presets = getPresets();
        SetProperty("Presets", presets);
    }
    return presets;
}

function getCurrentPresetFromStorage()
{
    const index = GetProperty<number>(currentPresetIndexString);
    return getPresetsFromStorage()[index];
}