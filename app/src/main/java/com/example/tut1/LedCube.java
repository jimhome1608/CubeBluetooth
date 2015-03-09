package com.example.tut1;

import android.graphics.Point;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;
import com.example.tut1.Bluetooth;

public class LedCube {

    public Point3D home3D;
    public ArrayList<Led> ledList = new ArrayList<Led>();
    public String direction = "xup";

    public void WalkThisWay() {
        int _delay = 100;
        boolean hitX = false;
        boolean hitY = false;
        boolean hitZ = false;
        while ( hitZ == false) {
            hitX = false;
            while (hitX == false) {
                hitX = Xplus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            hitY = false;
            while (hitY == false) {
                hitY = Yplus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            hitX = false;
            while (hitX == false) {
                hitX = Xminus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            hitY = false;
            while (hitY == false) {
                hitY = Yminus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            hitZ = Zplus();
            BlueToothWrite();
            SystemClock.sleep(_delay);
        }
        hitX = false;
        while (hitX == false) {
            hitX = Xplus();
            BlueToothWrite();
            SystemClock.sleep(_delay);
        }
        hitY = false;
        while (hitY == false) {
            hitY = Yplus();
            BlueToothWrite();
            SystemClock.sleep(_delay);
        }
        hitX = false;
        while (hitX == false) {
            hitX = Xminus();
            BlueToothWrite();
            SystemClock.sleep(_delay);
        }
        hitY = false;
        while (hitY == false) {
            hitY = Yminus();
            BlueToothWrite();
            SystemClock.sleep(_delay);
        }
    }

    public void WalkThisWayBasic() {
        int x = 0;
        int y = 0;
        int z = 0;
        Led led;
        Led prevLed = null;
        int _delay = 100;

        MoveTo(0,0,0);
        for (z=0;z<=3;z++) {
            for (x = 0; x <= 3; x++) {
                Xplus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            for (y = 1; y <= 3; y++) {
                Yplus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            for (x = 2; x >= 0; x--) {
                Xminus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            for (y = 2; y >= 0; y--) {
                Yminus();
                BlueToothWrite();
                SystemClock.sleep(_delay);
            }
            Zplus();
            BlueToothWrite();
            SystemClock.sleep(_delay);
        }

    }


    public void Step() {
        int _delay = 200;
        if (direction == "zup" ) {
            Zplus();
            direction = "xup";
            return;
        }
        if (direction == "xup" ) {
            if (Xplus()==true)
                direction = "yup";
            return;
        }
        if (direction == "yup" ) {
            if (Yplus()==true)
                direction = "xdown";
            return;
        }
        if (direction == "xdown" ) {
            if (Xminus()==true)
                direction = "ydown";
            return;
        }
        if (direction == "ydown" ) {
            if (Yminus()==true)
                direction = "zup";
            return;
        }
        if (direction != "stop" ) {
            BlueToothWrite();
            SystemClock.sleep(_delay);
        }
        if (home3D.x ==0 && home3D.y == 0 && home3D.z == 3)
            direction = "stop";
    }

    public LedCube() {
        home3D = new Point3D(0, 0, 0);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                for (int z = 0; z < 4; z++) {
                    Led led = new Led(x, y, z, 0, 0, 0);
                    ledList.add(led);
                }
                ;
            }
        }
        setAllChanged(false);
    }

    public void BlueToothWrite() {
        Led led;
        String result = "";
        for (int i = 0; i < 64; i++) {
            led = (Led) ledList.get(i);
            if (led.changed == false)
                continue;
            // Log.v("x", led.asCommand());
            result = result+led.asCommand();
        };
        setAllChanged(false);
        Bluetooth.write(result);
    }

    public String GetLedCommands() {
        Led led;
        String result = "";
        for (int i = 0; i < 64; i++) {
            led = (Led) ledList.get(i);
            if (led.changed == false)
                continue;
            // Log.v("x", led.asCommand());
            result = result+led.asCommand();
        };
        setAllChanged(false);
        return result;
    }


    public void MoveTo (int x, int y, int z) {
        home3D.x = x;
        home3D.y = y;
        home3D.z = z;
    }

    public int LevelZ(int _z) {
        AllOff();
        Led led;
        for (int x = 0; x < 4; x++) {
            for (int y=0; y<4; y++) {
                led = getLed(x,y, home3D.z);
                led.turnOn();
            }
        }
        return 0;
    }

    public int WallX(int _x) {
        AllOff();
        Led led;
        for (int z = 0; z < 4; z++) {
            for (int y=0; y<4; y++) {
                led = getLed(home3D.x,y, z);
                led.turnOn();
            }
        }
        return 0;
    }

    public int WallY(int _y) {
        AllOff();
        Led led;
        for (int x = 0; x < 4; x++) {
            for (int z=0; z<4; z++) {
                led = getLed(x,home3D.y, z);
                led.turnOn();
            }
        }
        return 0;
    }

    public int AllOff() {
        Led led;
        for (int i = 0; i < 64; i++) {
            led = (Led) ledList.get(i);
            if (led.hasColor()) {
                led.changed = true;
                led.b = 0;
                led.g = 0;
                led.r = 0;
            };
        }
        //MoveTo(0,0,0);
        return 0;
    }

    public void LineTo(Point3D p) {
        DrawLine(home3D, p);
        MoveTo(p.x,p.y,p.z);
    }

    public void LineTo(int x, int y, int z) {
        DrawLine(home3D, new Point3D(x,y,z));
        MoveTo(x, y, z);
    }

    public boolean Xplus(){
        Led led;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOff();
        home3D.x++;
        if (home3D.x > 3)
            home3D.x = 3;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOn();
        return (home3D.x == 3);

    }

    public boolean Xminus(){
        Led led;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOff();
        home3D.x--;
        if (home3D.x < 0)
            home3D.x = 0;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOn();
        return (home3D.x == 0);
    }

    public boolean Zplus(){
        Led led;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOff();
        home3D.z++;
        if (home3D.z > 3)
            home3D.z = 3;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOn();
        return (home3D.z == 3);
    }

    public boolean Zminus(){
        Led led;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOff();
        home3D.z--;
        if (home3D.z < 0)
            home3D.z = 0;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOn();
        return (home3D.z == 0);
    }

    public boolean Yplus(){
        Led led;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOff();
        home3D.y++;
        if (home3D.y > 3)
            home3D.y = 3;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOn();
        return (home3D.y == 3);
    }

    public boolean Yminus(){
        Led led;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOff();
        home3D.y--;
        if (home3D.y < 0)
            home3D.y = 0;
        led = getLed(home3D.x,home3D.y,home3D.z);
        led.turnOn();
        return (home3D.y == 0);
    }

    public  int DrawLine(Point3D p1, Point3D p2 ) {
        Led led;
        Point3D p = new Point3D(p1.x,p1.y,p1.z);
        led = getLed(p.x,p.y,p.z);
        led.b = 255;
        led.changed = true;
        if (p.x < p2.x) p.x++;
        if (p.x > p2.x) p.x--;
        if (p.y < p2.y) p.y++;
        if (p.y > p2.y) p.y--;
        if (p.z < p2.z) p.z++;
        if (p.z > p2.z) p.z--;
        led = getLed(p.x,p.y,p.z);
        led.b = 255;
        led.changed = true;
        if (p.x < p2.x) p.x++;
        if (p.x > p2.x) p.x--;
        if (p.y < p2.y) p.y++;
        if (p.y > p2.y) p.y--;
        if (p.z < p2.z) p.z++;
        if (p.z > p2.z) p.z--;
        led = getLed(p.x,p.y,p.z);
        led.b = 255;
        led.changed = true;
        if (p.x < p2.x) p.x++;
        if (p.x > p2.x) p.x--;
        if (p.y < p2.y) p.y++;
        if (p.y > p2.y) p.y--;
        if (p.z < p2.z) p.z++;
        if (p.z > p2.z) p.z--;
        led = getLed(p.x,p.y,p.z);
        led.b = 255;
        led.changed = true;
        return 1;
    }

    public Led getLed(int _x, int _y, int _z) {
        Led result = null;
        for (int idx=0;idx<64;idx++) {
            result =  (Led) ledList.get(idx);
            if (result.x==_x && result.y ==_y && result.z==_z) {
                 result =  (Led) ledList.get(idx);
                 break;
                }
        }
        return result;
    }

    public int setAllChanged(boolean _changed) {
        Led led;
        for (int i = 0; i < 64; i++) {
            led = (Led) ledList.get(i);
            led.changed = _changed;
        }
        return 0;
    }

    public int invert() {
        Led led;
        for (int i = 0; i < 64; i++) {
            led = (Led) ledList.get(i);
            led.invert();
        }
        return 0;
    }

    public int randomChanges() {
        Led led;
        Random rn = new Random();
        int Idx = rn.nextInt(64);
        led = (Led) ledList.get(Idx);
        led.g = 255;
        led.changed = true;
        return Idx;
    }


    public class Led {
        public int x = 0;
        public int y = 0;
        public int z = 0;
        public int r = 0;
        public int g = 0;
        public int b = 0;
        public boolean changed = false;

        public Led(int _x, int _y, int _z, int _r, int _g, int _b){
            x = _x;
            y = _y;
            z = _z;
            r = _r;
            g = _g;
            b = _b;
        }

        public boolean hasColor()
        {
           boolean _result = false;
           if (r != 0) _result = true;
           if (g != 0) _result = true;
           if (b != 0) _result = true;
           return _result;
        }

        public void invert()
        {
            if (hasColor())
                turnOff();
            else
                turnOn();
            changed = true;
        }



        public void turnOff() {
            if (hasColor()) {
                changed = true;
                r = 0;
                g = 0;
                b = 0;
            }
        }

        public void turnOn() {
            if (! hasColor()) {
                changed = true;
                r = 0;
                g = 0;
                b = 255;
            }
        }

        public String asCommand() {
            String red =  Integer.toHexString(r).toUpperCase();
            if (red.length() < 2) red = '0'+red;
            String green = Integer.toHexString(g).toUpperCase();
            if (green.length() < 2) green = '0'+green;
            String blue = Integer.toHexString(b).toUpperCase();
            if (blue.length() < 2) blue = '0'+blue;
            return String.format("{%d%d%d%s%s%s}", x, y,z,red,green,blue);
        }
    }

    }
