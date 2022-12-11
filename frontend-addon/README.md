# About

This folder contains all code related to the Google Add-on

# Useful links

- [Google App Script Introduction](https://developers.google.com/apps-script)
- [Google Calendar](https://calendar.google.com/)
- [AppScript](https://script.google.com/)

# How to use

1. Clasp is a CLI to upload the addon code.
2. Run `npm install clasp` to install Clasp. You might want to add `--global` attribute.
3. Run `clasp login` to login to Clasp. You will need to authorize with your Google Account.
4. Run `clasp create --type standalone` to create a clasp connection with your Google account.
5. You should see `.clasp.json` file. It contains the `scriptid` of your project.

# Deployment
1. After deploying ngrok, you will need to update the application url in few locations each time:
   1. In [appscript.json](appsscript.json):
      1. In the `urlFetchWhitelist` array. The url needs to be in the https format.
      2. In the `openLink` field.
   2. In the [consts.ts](consts.ts) file in `MAINURL` field.
2. To deploy the Add-on to [Google Calendar](https://calendar.google.com/) you need to visit [AppScript](https://script.google.com/).
   1. Navigate to your project in `my projects` section.
      1. On the main screen, select `deploy` and then `test deployments`.
         1. Make sure `The latest code` option is selected in the `deployments` field.
         2. In the `Application(s): Calendar` section press `install`.
3. The Add-on should be ready to use in [Google Calendar](https://calendar.google.com/).

# Usage
1. To apply the changes in code, run `clasp push`.
2. When pushing for the first time, agree to overwriting the manifest by confirming with `y`
3. Visit [Google Calendar](https://calendar.google.com/).
4. Press the "+" icon in the Add-on tab on the right part of the display.
