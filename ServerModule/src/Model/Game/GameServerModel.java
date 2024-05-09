package Model.Game;

import DataTypes.Effort;
import Util.PropertyChangeSubject;

import java.util.ArrayList;

public interface GameServerModel extends PropertyChangeSubject
{
    ArrayList<Effort> getEfforts();
}
