function GetProperty<T>(name : string) : T
{
   const unparsed = PropertiesService.getUserProperties().getProperty(name);
   const parsed = JSON.parse(unparsed);
   return parsed;
}

function SetProperty(name : string, value : unknown)
{
    const parsed = JSON.stringify(value);
    PropertiesService.getUserProperties().setProperty(name, parsed);
}