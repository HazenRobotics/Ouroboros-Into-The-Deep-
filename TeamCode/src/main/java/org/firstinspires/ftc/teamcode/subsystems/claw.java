package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class claw  extends LinearOpMode {
    public Servo claw;

    @Override
    public void runOpMode() {
        Servo rClaw = hardwareMap.get(Servo.class, "rClaw");
        Servo lClawClaw = hardwareMap.get(Servo.class, "lClaw");
        waitForStart();
        //Uncomment when you want to work on this. Armaan commented this out so I could test code.
//        while(!isStopRequested()){
//            if(gamepad.a)
//        }
    }
}
