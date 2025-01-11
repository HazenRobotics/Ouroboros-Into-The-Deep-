package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.tuning.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Bucket;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.DoubleHorizontalExtendo;
import org.firstinspires.ftc.teamcode.subsystems.Imu;
import org.firstinspires.ftc.teamcode.subsystems.Lift;
import org.firstinspires.ftc.teamcode.subsystems.Pivot;
import org.firstinspires.ftc.teamcode.utils.DriverHubHelp;
import org.firstinspires.ftc.teamcode.utils.GamepadEvents;

@Autonomous(name = "Auto")
public class SampleAuto extends LinearOpMode{

    private GamepadEvents controller;
        private Imu imu;
        private DriverHubHelp screen;
        private Claw claw;
        private DoubleHorizontalExtendo extendo;
        private Lift lift;
        private Bucket bucket;
        private Pivot pivot;


    @Override
    public void runOpMode() throws InterruptedException {
        imu = new Imu(hardwareMap);
        screen = new DriverHubHelp();
        MecanumDrive robot = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        claw = new Claw(hardwareMap);
        lift = new Lift(hardwareMap, "lift", "lift");
        extendo = new DoubleHorizontalExtendo(hardwareMap, "hExtendo", "hExtendo");
//       climb = new Climb(hardwareMap,"climb");
        bucket = new Bucket(hardwareMap, "bucket");
        pivot = new Pivot(hardwareMap, "pivot", "pivot");

        waitForStart();
        Actions.runBlocking(
             robot.actionBuilder(new Pose2d(0, 0, 0))
////                     //fix this so I don't need to through an expection
                     .strafeTo(new Vector2d(0, 5))
                .stopAndAdd(()-> {
                    try {
                       pivot.toggle();
                    } catch (InterruptedException e) {
                       telemetry.addLine("Pivot toggle interrupted");
                   }
                })
                     .build());
                    sleep(500);
                Actions.runBlocking(
                robot.actionBuilder(new Pose2d(0, 5, 0))
                     .stopAndAdd(new InstantAction(() ->lift.basicToggle()))
                     .lineToX(-13)
//                        .turn(Math.toRadians(45))
                        .build());
                sleep(100);

//        Actions.runBlocking(
//                robot.actionBuilder(new Pose2d(-14, 5, Math.toRadians(45)))
//                        .lineToX(-30)
//                        .build());
//        sleep(500);

        Actions.runBlocking(
                robot.actionBuilder(new Pose2d(-13, 5, Math.toRadians(0)))
                .lineToX(-18)
                .build());
                sleep(500);

        Actions.runBlocking(
                robot.actionBuilder(new Pose2d(-18, 5, Math.toRadians(0)))
                        .turn(Math.toRadians(45))
                        .build());
        sleep(500);
        //score first sample
        Actions.runBlocking(
                robot.actionBuilder(new Pose2d(-13, 5, Math.toRadians(0)))
                     .stopAndAdd(new InstantAction(() -> bucket.toggle()))
                        .stopAndAdd(new InstantAction(() -> bucket.toggle()))

                .build());
//        sleep(4000);
//
//        Actions.runBlocking(
//                robot.actionBuilder(new Pose2d(-30, 5, Math.toRadians(45)))
//                        .lineToX(-27)
//                        .build());
//        sleep(10);
//
//
//        Actions.runBlocking(
//                robot.actionBuilder(new Pose2d(-27, 5, Math.toRadians(45)))
//                        .turn(Math.toRadians(-135))
//                        .build());

        while(opModeIsActive())
        {
            telemetry.addLine("Opmode Activited");
        }

    }
}
