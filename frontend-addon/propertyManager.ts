class PropertyManager {

    static getProperty<T>(name: string): T {
        const unparsed = PropertiesService.getUserProperties().getProperty(name);
        return JSON.parse(unparsed);
    }

    static setProperty(name: string, value: unknown) {
        const parsed = JSON.stringify(value);
        PropertiesService.getUserProperties().setProperty(name, parsed);
    }

    static resetProperties() {
        this.setProperty(eventNameString, "PlanIt Event");
        this.setProperty(minDateString, msSinceEpocToday.valueOf());
        this.setProperty(maxDateString, msSinceEpocToday.valueOf() + weekInMs);
        this.setProperty(durationString, "1:45");
        this.setProperty(errorString, []);
        this.setProperty(addUserEmailString, "");
        this.setProperty(presetString, Array.of(defaultPreset));
        this.setProperty(usersString, []);
        this.setProperty(currentPresetIndexString, 0)
    }
}