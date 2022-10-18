package Server.Commands;

import Commands.Interfaces.ICommandManager;
import Commands.Model.CommandModel;
import Server.ServerContainer;

public class Stop extends CommandModel implements ICommandManager
{
    public Stop()
    {
        operationName = this.getClass().getSimpleName();
    }

    public void execute()
    {
        ServerContainer.getServer().stop();
    }
}
