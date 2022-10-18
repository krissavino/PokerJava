package Server.Commands;

import Commands.Interfaces.ICommandManager;
import Commands.Model.CommandModel;
import Server.ServerContainer;

public class Empty extends CommandModel implements ICommandManager
{
    public Empty()
    {
        operationName = this.getClass().getSimpleName();
    }

    public void execute()
    {

    }
}
