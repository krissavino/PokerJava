package Server.Commands;

import Commands.Interfaces.ICommandManager;
import Commands.Model.CommandModel;

public class GiveAwayCards extends CommandModel implements ICommandManager
{
    public GiveAwayCards()
    {
        operationName = this.getClass().getSimpleName();
    }

    public void execute()
    {

    }
}
