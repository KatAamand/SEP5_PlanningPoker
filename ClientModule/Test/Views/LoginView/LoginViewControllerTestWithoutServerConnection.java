package Views.LoginView;

import Application.ClientFactory;
import Networking.ClientConnection_RMI;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class LoginViewControllerTestWithoutServerConnection
{
  private static ClientConnection_RMI client;

  @BeforeEach void setUp()
  {
    //Initializes the javaFx component library, and starts the server which the client uses to connect with.
    Platform.startup(() -> {
      try {
        client = ClientFactory.getInstance().getClient();
      } catch (RemoteException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Test public void testClientThrowsRunTimeExceptionWhenMissingServerConnection () {
    assertThrows(RuntimeException.class, () -> ClientFactory.getInstance().getClient());
  }

}