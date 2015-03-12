A context is a set of available commands a user can run as well as child contexts. Some examples are the main menu context and the game context.

Grouping commands by context allows commands to use identical input without overlapping as well as to partition the complexity of the mud.

For example, in the context of a conversation with an NPC the input "n" might be interpreted as "no", whereas in the general game context "n" might be interpreted as "go north".

Another example: in the context of a conversation with NPC "Alice", "n" means "say no to Alice", but in the context of a conversation with NPC "Bob", "n" means "say no to Bob".

Contexts can have:
  * A menu
  * A custom prompt
  * Commands
  * Automated "on entry" commands (e.g. print a message or menu)
  * A custom default command (i.e. when input doesn't match any known command)
  * A maximum number of entries (i.e. login/password validation)

It should be quite simple to model any custom interaction within, not just a game, but a server:
  * Server menus
  * In-game menus (e.g. a computer terminal)
  * Bulletin boards
  * Script/Macro editing
  * Worlds/Dimensions/Zones

Currently my "reference implementation" of flexmud has several contexts:
  * Welcome - prints welcome message (on-entry), switches to login context (on-entry)
  * Login - prompts for login, switches to password context
  * Password - prompts for password, switches to login validation context
  * Login Validation - checks login and either disconnects, sends back to login or sends to the main menu context
  * Main menu - shows a menu to choose to Quit, Character Management or Play
  * Character Management - shows a menu to choose to create, edit or delete characters
  * Create character - empty so far
  * Edit character - empty so far
  * Delete character - empty so far
  * Game - Where users actually play.