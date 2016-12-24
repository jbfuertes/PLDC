package com.example.johnberlinfuertes.pldc;

/**
 * Created by berlin on 12/22/2016.
 */

public class Code {
    int _id;
    String _code;

    public Code(){

    }

    public Code(String code) {
        this._code = code;
    }

    public Code(int id,String code) {
        this._code = code;
        this._id = id;
    }

    public String get_Code() {
        return _code;
    }

    public void set_Code(String _Code) {
        this._code = _Code;
    }
}
