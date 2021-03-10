package com.mdiluca.ptdma.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdiluca.ptdma.Models.Event;
import com.mdiluca.ptdma.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarItemAdapter extends BaseAdapter {
    private int layout;
    private Context context;
    private List<Event> items;


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

    public CalendarItemAdapter(Context context, int layout, List<Event> items ){
        this.context = context;
        this.layout = layout;
        this.items = items;
    }
    @Override
    public int getCount() {
        return this.items.size();
    }
    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v;
        if (view==null){
            //Inflamos el layout de nuestros items en nuestra vista v
            LayoutInflater inflater = LayoutInflater.from(this.context);
            v = inflater.inflate(R.layout.calendar_item,null);
        }
        //Partimos de la vista que nos pasa Android, que ya tiene cargado el layout.
        else v=view;
        //Obtenemos el nombre de la lista según la posición que nos pasan.
        Event selectedEvent = items.get(position);
        //instanciamos el ImageView y el TextView de nestro Layout pars los items.
        TextView titleText = v.findViewById(R.id.title);
        TextView dateText = v.findViewById(R.id.date);
        //El imageView podríamos buscar una imagen de la persona y colocarlo en vez de la imagen por defecto
        //Pero en este ejemplo eso no está implementado.
        //En el textView si ponemos el nombre.
        titleText.setText(selectedEvent.getTitle());
        dateText.setText(dateFormat.format(selectedEvent.getDate()));
        //Devolvemos la vista (nuestro layout) actualizada para la posición requerida.
        return v;
    }
}