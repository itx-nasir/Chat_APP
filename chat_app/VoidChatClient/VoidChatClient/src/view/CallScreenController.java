/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.TestVoiceChat;

public class CallScreenController {

    @FXML
    private Button butEndingCall;

    @FXML
    void btnEndCall(ActionEvent event) {
        TestVoiceChat u = new TestVoiceChat();
        u.stopServer();
    }

}
