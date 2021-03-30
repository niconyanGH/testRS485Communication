import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control. *;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import ksxsdk. *;

public class App extends Application {

    public ComboBox cbportList;
    public Button btnOpen;

    private Button btnScan;
    private Label lbinfo;
    private Label lbscaninfo;
    public Button btnDvc;

    private ScrollPane sclPanSwitch;
    private FlowPane flwPanSwitch;
    private Label lblSltSwt;
    private Label lblSltSwt1;
    private RadioButton rbtnSwtCtrl1;
    private RadioButton rbtnSwtCtrl2;
    private TextField txtAreaSwtTime;
    private Button btnSwtOn;
    private Button btnSwtOff;

    private ScrollPane sclPanReact;
    private FlowPane flwPanReact;
    private Label lblSltReact;
    private Label lblSltReact1;
    private RadioButton rbtnReactCtrl1;
    private RadioButton rbtnReactCtrl2;
    private RadioButton rbtnReactCtrl3;
    private RadioButton rbtnReactCtrl4;
    private TextArea txtAreaReactTime;
    private Button btnReactOpen;
    private Button btnReactClose;
    private Button btnReactStop;

    private ReadSensorThread readThread;
    private ActuatorNode mActuatorNode;
    STDModbusRTUMaster mMaster = new STDModbusRTUMaster();

    private List<PaneSensor> mPanesensorlist = new ArrayList<>();

    private PaneSensor switchPanePreSelect;
    private PaneSensor reactablePanePreSelect;

    String defaultPaneColor = "#7986CB";
    String selectedPaneColor = "#BA68C8";
    String scaningPaneColor = "#CDDC39";
    
    @Override public void start(Stage stage)throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("mainFrame.fxml"));
        sclPanSwitch = (ScrollPane)root.lookup("#sclPaneSwitch");
        flwPanSwitch = (FlowPane)sclPanSwitch.getContent();
        sclPanReact = (ScrollPane)root.lookup("#sclPaneReactable");
        flwPanReact = (FlowPane)sclPanReact.getContent();

        cbportList = (ComboBox)root.lookup("#comboportList");
        btnOpen = (Button)root.lookup("#btnopen");

        btnScan = (Button)root.lookup("#button_scan");
        lbinfo = (Label)root.lookup("#label_nodeinfo");
        lbscaninfo = (Label)root.lookup("#label_scan");
        btnDvc = (Button)root.lookup("#button_device");

        lblSltSwt = (Label)root.lookup("#lblSelectedSwitch");
        lblSltSwt1 = (Label)root.lookup("#lblSelectedSwitch1");
        rbtnSwtCtrl1 = (RadioButton)root.lookup("#rbtnSwtCtrl1");
        rbtnSwtCtrl2 = (RadioButton)root.lookup("#rbtnSwtCtrl2");
        txtAreaSwtTime = (TextField)root.lookup("#txtAreaSwtTime");
        btnSwtOn = (Button)root.lookup("#btnSwtOn");
        btnSwtOff = (Button)root.lookup("#btnSwtOff");

        lblSltReact = (Label)root.lookup("#lblSelectedReactable");
        lblSltReact1 = (Label)root.lookup("#lblSelectedReactable1");
        rbtnReactCtrl1 = (RadioButton)root.lookup("#rbtnReactCtrl1");
        rbtnReactCtrl2 = (RadioButton)root.lookup("#rbtnReactCtrl2");
        rbtnReactCtrl3 = (RadioButton)root.lookup("#rbtnReactCtrl3");
        rbtnReactCtrl4 = (RadioButton)root.lookup("#rbtnReactCtrl4");
        txtAreaReactTime = (TextArea)root.lookup("#txtAreaReactTime");
        btnReactOpen = (Button)root.lookup("#btnReactOpen");
        btnReactClose = (Button)root.lookup("#btnReactClose");
        btnReactStop = (Button)root.lookup("#btnReactStop");

        SerialPort[] ports = SerialPort.getCommPorts();
        btnScan.setDisable(true);
        btnDvc.setDisable(true);

        controlOn(true);

        for (SerialPort pp : ports) {
            cbportList.getItems().add(pp.getDescriptivePortName());
        }

        btnOpen.setOnAction((event) -> {
            System
                .out
                .println(cbportList.getSelectionModel().getSelectedItem());
            for (SerialPort pp : ports) {
                if (pp.getDescriptivePortName() == cbportList.getSelectionModel().getSelectedItem()) 
                    OpenPort(pp.getSystemPortName());
                }
            });

        btnScan.setOnAction((event) -> {
            NodeScan();
            btnOpen.setDisable(true);
            btnScan.setDisable(true);
            btnDvc.setDisable(false);
        });
        btnDvc.setOnAction((event) -> {
            DeviceScan();
            btnScan.setDisable(true);
            btnDvc.setDisable(true);
            controlOn(false);
            if(readThread == null) {
                readThread = new ReadSensorThread(mActuatorNode, mPanesensorlist);
                readThread.StartThread();
            }
        });
        btnSwtOn.setOnAction((event) -> {
            if(rbtnSwtCtrl1.isSelected() == true){
                mActuatorNode.controlSwitch(switchPanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_SWITCH_ON, 0, (long)0, (short)0);
            } else if(rbtnSwtCtrl2.isSelected() == true) {
                mActuatorNode.controlSwitch(switchPanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_SWITCH_TIMED_ON, 0, Long.parseLong(txtAreaSwtTime.getText()), (short)0);
            }
        });
        btnSwtOff.setOnAction((event) -> {
            if(rbtnReactCtrl1.isSelected() == true){
                mActuatorNode.controlReactable(switchPanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_OPEN, 0, (long)0, 25, 0, 0);
            }else if(rbtnReactCtrl2.isSelected() == true){
                mActuatorNode.controlReactable(switchPanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_TIMED_CLOSE, 0, Long.parseLong(txtAreaReactTime.getText()), 25, 0, 0);
            }
                
        });

        btnReactOpen.setOnAction((event) -> {
            if(rbtnReactCtrl1.isSelected() == true){
                mActuatorNode.controlReactable(reactablePanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_OPEN, 0, (long)0, 25, 0, 0);
            } else if(rbtnReactCtrl2.isSelected() == true){
                mActuatorNode.controlReactable(reactablePanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_TIMED_OPEN, 0, Long.parseLong(txtAreaReactTime.getText()), 25, 0, 0);
            } else if(rbtnReactCtrl3.isSelected() == true){
                mActuatorNode.controlReactable(reactablePanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_SET_POSITION, 0, (long)0, 25, Integer.parseInt(txtAreaReactTime.getText()), 0);
            } else if(rbtnReactCtrl4.isSelected() == true){
                mActuatorNode.controlReactable(reactablePanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_SET_CONFIG, 0, (long)0, 25, 0, Integer.parseInt(txtAreaReactTime.getText()));
            }
        });

        btnReactStop.setOnAction((event) -> {
            mActuatorNode.controlReactable(reactablePanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_OFF_STOP, 0, (long)0, 0, 0, 0);
    });

        btnReactClose.setOnAction((event) -> {
            if(rbtnReactCtrl1.isSelected() == true){
                mActuatorNode.controlReactable(reactablePanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_CLOSE, 0, Long.parseLong(txtAreaReactTime.getText()), 0, 0, 0);
            } else if(rbtnReactCtrl2.isSelected() == true){
                mActuatorNode.controlReactable(reactablePanePreSelect.myActuator, ACTUATOR_COMMAND.OPERATION_RETRACTABLE_TIMED_CLOSE, 0, Long.parseLong(txtAreaReactTime.getText()), 25, 0, 0);
            }
        });
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void OpenPort(String tmpPort) {
        if (mMaster.Open(tmpPort, 9600) == false) {
            System
                .out
                .println("통신포트를 열수 없읍니다. ");
        } else {
            cbportList.setDisable(true);
            btnOpen.setDisable(true);
            btnScan.setDisable(false);
        }
    }

    public void NodeScan() {
        PRODUCT_TYPE mNodetype = PRODUCT_TYPE.NONE;
        int slaveid = 0;
        String strinfo;
        for (int i = 0; i < 20; i++) {
            strinfo = "국번= " + i;
            mNodetype = KSX326xCommon.IsKSXNode(i, mMaster);
            if (mNodetype != PRODUCT_TYPE.NONE) {
                slaveid = i;
                strinfo += " , 장비연결됨...";
                i = 10000;
            } else {
                strinfo += " , 장비없음...";
            }
            lbscaninfo.setText(strinfo);
        }

        if (mNodetype == PRODUCT_TYPE.ACTUATORNODE) {
            mActuatorNode = new ActuatorNode(slaveid, mMaster);
            String cs = " 기본정보 읽기 실패.";
            if (mActuatorNode.ReadNodeInformation() == true) {
                cs = " CertificateAuthority = " + mActuatorNode.mNodeInfo.CertificateAuthority +
                        "\r\n";
                cs += " CompanyCode = " + mActuatorNode.mNodeInfo.CompanyCode + "\r\n";
                cs += " ProductType = " + mActuatorNode.mNodeInfo.ProductType + "\r\n";
                cs += " ProductCode = " + mActuatorNode.mNodeInfo.ProductCode + "\r\n";
                cs += " ChannelNumber = " + mActuatorNode.mNodeInfo.ChannelNumber + "\r\n";
                cs += " ProtocolVersion = " + mActuatorNode.mNodeInfo.ProtocolVersion + "\r\n";
            }

            lbinfo.setText(cs);
        }
    }

    public void DeviceScan() {
        try {
            if (mActuatorNode.ReadDeviceList() == true) {
                if (mActuatorNode.createDevice() == true) {
                    System
                        .out
                        .println(" 디바이스갯수 = " + mActuatorNode.mDevices.size());

                    for (ActuatorDev msdev : mActuatorNode.mActuatorDevices) {
                        PaneSensor ps = PaneSensor.Create(this, msdev);
                        ps.SetBKColor(Color.valueOf(defaultPaneColor));
                        if (msdev.mDevice.getActuatorType() == ACTUATOR_TYPE.SWITCH) {
                            flwPanSwitch.getChildren().add(ps);
                            mPanesensorlist.add(ps);
                            ps.setOnMouseClicked((event) -> {
                                OnSwitchPaneClick(ps);
                            });
                        } else if(msdev.mDevice.getActuatorType() == ACTUATOR_TYPE.RETRACTABLE){
                            flwPanReact.getChildren().add(ps);
                            mPanesensorlist.add(ps);
                        
                            ps.setOnMouseClicked((event) -> {
                                OnReactablePaneClick(ps);
                            });
                        }
                    }
                }
            }
        } catch (IOException e) {}
    }
    
    public void OnSwitchPaneClick(PaneSensor ps) {
        if(switchPanePreSelect == null ){
            ps.SetBKColor(Color.valueOf(selectedPaneColor));
            switchPanePreSelect = ps;
            readThread.prePaneSensor1 = ps;
            lblSltSwt1.setText(ps.myActuator.mDevice.Name);
        } else if(switchPanePreSelect != ps){
            ps.SetBKColor(Color.valueOf(selectedPaneColor));
            switchPanePreSelect.SetBKColor(Color.valueOf(defaultPaneColor));
            switchPanePreSelect = ps;
            readThread.prePaneSensor1 = ps;
            lblSltSwt1.setText(ps.myActuator.mDevice.Name);
        }
    }

    public void OnReactablePaneClick(PaneSensor ps) {
        if(reactablePanePreSelect == null ){
            ps.SetBKColor(Color.valueOf(selectedPaneColor));
            reactablePanePreSelect = ps;
            readThread.prePaneSensor2 = ps;
            lblSltReact1.setText(ps.myActuator.mDevice.Name);
            
        } else if(reactablePanePreSelect != ps) {
            ps.SetBKColor(Color.valueOf(selectedPaneColor));
            reactablePanePreSelect.SetBKColor(Color.valueOf(defaultPaneColor));
            reactablePanePreSelect = ps;
            readThread.prePaneSensor2 = ps;
            lblSltReact1.setText(ps.myActuator.mDevice.Name);
        }
    }

    


    public void controlOn(boolean ctrl) {
        lblSltSwt.setDisable(ctrl);
        lblSltSwt1.setDisable(ctrl);
        rbtnSwtCtrl1.setDisable(ctrl);
        rbtnSwtCtrl2.setDisable(ctrl);
        txtAreaSwtTime.setDisable(ctrl);
        btnSwtOn.setDisable(ctrl);
        btnSwtOff.setDisable(ctrl);

        lblSltReact.setDisable(ctrl);
        lblSltReact1.setDisable(ctrl);
        rbtnReactCtrl1.setDisable(ctrl);
        rbtnReactCtrl2.setDisable(ctrl);
        rbtnReactCtrl3.setDisable(ctrl);
        rbtnReactCtrl4.setDisable(ctrl);
        txtAreaReactTime.setDisable(ctrl);
        btnReactOpen.setDisable(ctrl);
        btnReactClose.setDisable(ctrl);
        btnReactStop.setDisable(ctrl);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
