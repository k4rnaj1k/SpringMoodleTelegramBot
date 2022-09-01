# Telegram bot that works with moodle rest api.
To run locally simply execute:
``docker-compose --env-file .\telegram-bot.env up``

Or set up these env vars:
<table>
<tr><th>VARIABLE</th><th>DESCRIPTION</th></tr>
<tr><td>DB_USR</td><td>database user</td></tr>
<tr><td>DB_PASS</td><td>database password</td></tr>
<tr><td>DB_URL</td><td>database url(jdbc)</td></tr>
<tr><td>BOT_TOKEN</td><td>telegram bot token</td></tr>
<tr><td>BOT_USR</td><td>bot username</td></tr>
<tr><td>URL</td><td>current server's url</td></tr>
<tr><td>MOODLE_URL</td><td>moodle services url</td></tr>
<tr><td>ADMIN_CHATID</td><td>Chat id of person that can force the events list to update</td></tr>
</table>

**Currently, app works only with postgresql.**