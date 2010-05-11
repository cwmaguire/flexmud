/**************************************************************************************************
 * Copyright 2009 Chris Maguire (cwmaguire@gmail.com)                                             *
 *                                                                                                *
 * Flexmud is free software: you can redistribute it and/or modify                                *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 *                                                                                                *
 * Flexmud is distributed in the hope that it will be useful,                                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 *                                                                                                *
 * You should have received a copy of the GNU General Public License                              *
 * along with flexmud.  If not, see <http://www.gnu.org/licenses/>.                               *
 **************************************************************************************************/
package flexmud.engine.context;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "context")
public class Context {
    private static final Logger LOGGER = Logger.getLogger(Context.class);
    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String CHILD_GROUP_PROPERTY = "childGroup";
    public static final String PARENT_GROUP_PROPERTY = "parentGroup";
    public static final String COMMAND_CLASS_NAME_ALIASES_PROPERTY = "contextCommands";
    public static final String ENTRY_MESSAGE_PROPERTY = "entryMessage";
    public static final String DOES_USE_CHARACTER_PROMPT_PROPERTY = "isCharacterPromptable";
    public static final String MAX_ENTRIES_PROPERTY = "maxEntries";
    public static final String MAX_ENTRIES_EXCEEDED_MESSAGE_PROPERTY = "maxEntriesExceededMessage";
    public static final String ENTRY_COMMAND_CLASS_NAME_PROPERTY = "entryCommandClassName";
    public static final String PROMPT_COMMAND_CLASS_NAME_PROPERTY = "promptCommandClassName";
    public static final String DEFAULT_COMMAND_CLASS_NAME_PROPERTY = "defaultCommandClassname";
    public static final String PROMPT_PROPERTY = "prompt";

    private long id;
    private String name;
    private ContextGroup childGroup;
    private ContextGroup parentGroup;
    private Set<ContextCommand> contextCommands = new HashSet<ContextCommand>();
    private String entryMessage;
    private int maxEntries = -1;
    private String maxEntriesExceededMessage = "";
    private String prompt;
    private boolean isSecure;

    private Map<String, ContextCommand> aliasCommandClasses = new HashMap<String, ContextCommand>();
    private Map<ContextCommandFlag, List<ContextCommand>> flaggedCntxtCmds = new HashMap<ContextCommandFlag, List<ContextCommand>>();

    public Context() {
    }

    public Context(String name) {
        this();
        this.name = name;
    }

    public void init() {
        mapFlaggedCommandClasses();
        mapAliasedCommandClasses();
    }

    private void mapFlaggedCommandClasses() {
        List<ContextCommand> contextCommandsList = new ArrayList<ContextCommand>(contextCommands);
        if (contextCommands != null && !contextCommands.isEmpty()) {

            Collections.sort(contextCommandsList, new Comparator<ContextCommand>() {
                @Override
                public int compare(ContextCommand cntxtCmd1, ContextCommand cntxtCmd2) {
                    int sequence1 = cntxtCmd1.getSequence();
                    int sequence2 = cntxtCmd2.getSequence();

                    // sort in reverse order except zero is always last
                    if (sequence1 == 0) {
                        return sequence2;
                    } else if (sequence2 == 0) {
                        return 0;
                    } else {
                        return sequence1 - sequence2;
                    }
                }
            });

            for (ContextCommand contextCommand : contextCommandsList) {
                mapFlaggedContxtCommands(contextCommand);
            }
        }
    }

    private void mapFlaggedContxtCommands(ContextCommand contextCommand) {
        List<ContextCommand> flaggedCntxtCmdsList;
        ContextCommandFlag flag = contextCommand.getContextCommandFlag();

        if (flag != null) {
            flaggedCntxtCmdsList = this.flaggedCntxtCmds.get(flag);
            if (flaggedCntxtCmdsList == null) {
                flaggedCntxtCmdsList = new ArrayList<ContextCommand>();
            }
            flaggedCntxtCmdsList.add(contextCommand);
            this.flaggedCntxtCmds.put(flag, flaggedCntxtCmdsList);
        }
    }

    private void mapAliasedCommandClasses() {
        if (contextCommands != null) {
            for (ContextCommand contextCommand : contextCommands) {
                for (ContextCommandAlias alias : contextCommand.getAliases()) {
                    aliasCommandClasses.put(alias.getAlias(), contextCommand);
                }
            }
        }
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "entry_message")
    public String getEntryMessage() {
        return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
        this.entryMessage = entryMessage;
    }

    @Column(name = "max_entries")
    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    @Column(name = "max_entries_exceeded_message")
    public String getMaxEntriesExceededMessage() {
        return maxEntriesExceededMessage;
    }

    public void setMaxEntriesExceededMessage(String maxEntriesExceededMessage) {
        this.maxEntriesExceededMessage = maxEntriesExceededMessage;
    }

    // allows the user to have a generic prompt instead of a specific prompt command
    @Column(name = "prompt")
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Column(name = "is_secure")
    public boolean isSecure() {
        return isSecure;
    }

    public void setSecure(boolean isSecure) {
        this.isSecure = isSecure;
    }

    // maps to the context_group that holds this context's children
    @OneToOne(targetEntity = ContextGroup.class, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "child_group_id")
    public ContextGroup getChildGroup() {
        return childGroup;
    }

    public void setChildGroup(ContextGroup childGroup) {
        this.childGroup = childGroup;
    }

    // maps to the group that holds the contexts for this context's parent and siblings
    @ManyToOne
    @JoinColumn(name = "parent_group_id", nullable = true)
    public ContextGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(ContextGroup parentGroup) {
        this.parentGroup = parentGroup;
    }


    @OneToMany(mappedBy = ContextCommand.CONTEXT_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<ContextCommand> getContextCommands() {
        return contextCommands;
    }

    public void setContextCommands(Set<ContextCommand> contextCommands) {
        this.contextCommands = contextCommands;
    }

    @Transient
    public List<ContextCommand> getFlaggedContextCommands(ContextCommandFlag flag) {
        return flaggedCntxtCmds.get(flag);
    }

    @Transient
    public Map<String, ContextCommand> getAliasContextCommands() {
        return aliasCommandClasses;
    }

    @Transient
    public ContextCommand getContextCommandForAlias(String alias) {
        return aliasCommandClasses.get(alias);
    }

    @Override
    public boolean equals(Object obj) {
        return Context.class.isAssignableFrom(obj.getClass()) && ((Context) obj).getId() == id;
    }
}
