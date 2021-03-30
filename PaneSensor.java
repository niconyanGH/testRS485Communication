import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import ksxsdk.*;
public class PaneSensor extends Pane
{
    public ActuatorDev myActuator;
    private Label lblName;
    private Label lblStatus;
    private Label lblR_Time;
    private Label lblL_Cmd;

    public static PaneSensor Create(Application mcls, ActuatorDev ma) throws IOException
    {
            Parent pp = FXMLLoader.load(mcls.getClass().getResource("panesensor.fxml"));
            return new PaneSensor(pp,ma);
    }

    public PaneSensor(Parent pp,ActuatorDev ma )  
    {
       super(pp);
       myActuator = ma;
       lblName=(Label)pp.lookup("#lblName");
       lblStatus=(Label)pp.lookup("#lblStatus");
       lblR_Time=(Label)pp.lookup("#lblRemainingTime");
       lblL_Cmd=(Label)pp.lookup("#lblLastCommand");
       
       UpdateStatus(false);
    }

    public void UpdateStatus(boolean isvaluesave)
    {
        Platform.runLater(() -> {
            try
            {
                lblName.setText(myActuator.mDevice.Name);
                lblStatus.setText("상태: " + KSX326xMetadata.GetStatusDescrition(STATUS_CODE.getEnum(myActuator.status)));
                lblR_Time.setText("남은시간: " + myActuator.remain_time);
                lblL_Cmd.setText("마지막 실행명령: " + myActuator.position);
            } catch ( Exception  ex) {
                System.out.println(ex.toString());
            }
        });
    }

    public void SetBKColor(Color mColor)
    {
       this.setBackground(new Background(new BackgroundFill(mColor, null, null)));
    }
}
