class Storage {

    static getPresets(): PresetDetails[] {
        console.log("getPresetsFromStorage")
        let presets = PropertyManager.getProperty<PresetDetails[]>(presetString);
        if (!presets || presets.length === 0 || this.theOnlyPresetIsTheDefaultOne(presets)) {
            const returned = API.getPresets();
            console.log(returned)
            if (returned === "404") {
                presets = Array.of(defaultPreset)
            } else {
                presets = returned;
                presets.unshift(defaultPreset);
            }
            PropertyManager.setProperty(presetString, presets);
        }
        console.log({presets})
        return presets;
    }


    static getCurrentPreset() {
        console.log("getCurrentPresetFromStorage")
        const index = PropertyManager.getProperty<number>(currentPresetIndexString);
        console.log({index})
        return this.getPresets()[index];
    }

    static updateGuests(presets: PresetDetails[], currentIndex: number) {
        console.log({presets, currentIndex});
        const preset = presets[currentIndex];
        const guests = preset.guests;
        PropertyManager.setProperty(usersString, guests);
    }
    static theOnlyPresetIsTheDefaultOne(presets: PresetDetails[]): boolean {
        return presets.length === 1 && JSON.stringify(defaultPreset) === JSON.stringify(presets[0]);
    }
}