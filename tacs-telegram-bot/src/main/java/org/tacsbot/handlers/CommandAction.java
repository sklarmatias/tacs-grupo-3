package org.tacsbot.handlers;

@FunctionalInterface
public interface CommandAction {
    void execute(Long id, String commandText);
}
