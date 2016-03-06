package com.example.pica.zmap;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.AndroidSupport;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Query;

import java.io.IOException;
import java.util.GregorianCalendar;

public class BaseDatos implements Parcelable {

    private ObjectContainer db;

    public BaseDatos(Context ctx) {
        try {
            db = Db4oEmbedded.openFile(dbConfig(), ctx.getExternalFilesDir(null) + "/posiciones.db4o");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected BaseDatos(Parcel in) {
    }

    public void store(Posicion p){
        db.store(p);
        db.commit();
    }

    public ObjectSet query(){
        Query consulta = db.query();
        consulta.constrain(Posicion.class);
        return consulta.execute();
    }

    public static final Creator<BaseDatos> CREATOR = new Creator<BaseDatos>() {
        @Override
        public BaseDatos createFromParcel(Parcel in) {
            return new BaseDatos(in);
        }

        @Override
        public BaseDatos[] newArray(int size) {
            return new BaseDatos[size];
        }
    };

    private EmbeddedConfiguration dbConfig() throws IOException {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new AndroidSupport());
        configuration.common().activationDepth(25);
        configuration.common().objectClass(GregorianCalendar.class).storeTransientFields(true);
        configuration.common().objectClass(GregorianCalendar.class).callConstructor(true);
        configuration.common().exceptionsOnNotStorable(false);
        configuration.common().objectClass(Posicion.class).objectField("fecha").indexed(true);
        return configuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
