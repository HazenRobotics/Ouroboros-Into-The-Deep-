package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift{
    public int Lift_SPEED = 20;

    private DcMotor lift;
    private Encoder liftEncoder;

    private int midPos = 1500;
    private int topPos = 3050;

    private int zeroPos = 0;
    private int count = 0;

//    public Lift(HardwareMap hw){
//        this(hw, "lift", "lift");
//    }

    /**
     * Primary constructor for the Lift Subsystem Class
     * @param hw [HardwareMap] Hardware map necessary to initialize motors.
     * @param liftName [String] Name of the left lift motor assigned in the configuration.
     */
    public Lift(HardwareMap hw, String leftLiftName, String liftEncoderName){
        lift = hw.get(DcMotor.class, leftLiftName);
        liftEncoder = new OverflowEncoder(new RawEncoder(hw.get(DcMotorEx.class, liftEncoderName)));

        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        lift.setTargetPosition(0);
        
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        lift.setPower(1);

    }
    public Lift(HardwareMap hw, String liftName){
        lift = hw.get(DcMotor.class, liftName);
    }
    public void setPower(double power){
        lift.setPower(power);
    }
    public double getPosition()
    {
        return liftEncoder.getPositionAndVelocity().position;
    }
    

    public void setPosition(int targetPosition){
        
        lift.setTargetPosition(targetPosition);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0.7);

    }

    public void tunePosition(int pos)
    {
        zeroPos += pos;
        midPos += pos;
        topPos += pos;
    }

    public void lower()
    {
        lift.setTargetPosition(zeroPos);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0.7);
        count = 0;
    }

    public void setNewTargetPosition(int changePosition)
    {
        int currTicks = (int)getPosition();

        lift.setTargetPosition(currTicks + changePosition);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(1.0);

    }

    public double getLiftMotorPower()
    {
        return lift.getPower();
    }

    public void toggle()
    {
        if(count == 0)
        {
            setPosition(midPos);
            count++;
        }else if(count == 1)
        {
            setPosition(topPos);
            count++;
        }else if(count == 2)
        {
            setPosition(midPos);
            count++;
        }else if(count == 3)
        {
            setPosition(0);
            count = 0;
        }
    }

    public void basicToggle()
    {
         if(count == 0)
        {
            setPosition(topPos);
            count++;
        }else if(count == 1) {
             setPosition(zeroPos);
             count = 0;
         }
    }


}
