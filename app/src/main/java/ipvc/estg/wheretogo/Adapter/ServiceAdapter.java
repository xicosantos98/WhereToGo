package ipvc.estg.wheretogo.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ipvc.estg.wheretogo.Admin.AppointmentList;
import ipvc.estg.wheretogo.Classes.Estado;
import ipvc.estg.wheretogo.Classes.ILoadMore;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.Classes.SimpleCallback;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.R;

class LoadingViewHolder extends RecyclerView.ViewHolder{
    public ProgressBar progressBar;

    public LoadingViewHolder(View itemView){
        super(itemView);
        progressBar = itemView.findViewById(R.id.progress_bar_items);
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder{
    public TextView morada, data, descricao, tecnico, estado;
    public ImageView color_estado;
    public Button btn_cancel, btn_reallocate;

    public ItemViewHolder (View itemView){
        super(itemView);
        morada = itemView.findViewById(R.id.service_card_address);
        data = itemView.findViewById(R.id.service_card_date);
        descricao = itemView.findViewById(R.id.service_card_description);
        tecnico = itemView.findViewById(R.id.service_card_technician);
        //estado = itemView.findViewById(R.id.service_card_status);
        color_estado = itemView.findViewById(R.id.estado_color);
        btn_cancel = itemView.findViewById(R.id.service_card_cancel);
        btn_reallocate = itemView.findViewById(R.id.service_card_realocate);
    }
}
public class ServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<Servico> servicos;
    int visibleThreshold=20;
    int lastVisibleServico, totalServicoCount;

    Activity a;

    public ServiceAdapter(RecyclerView recyclerView, final Activity activity, List<Servico> servicos) {
        this.activity = activity;
        this.servicos = servicos;

        a = activity;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalServicoCount = linearLayoutManager.getItemCount();
                lastVisibleServico = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalServicoCount <= (lastVisibleServico+visibleThreshold)){
                    if(loadMore!=null)
                        loadMore.onLoadMore();
                }
                isLoading = true;
            }

        });

    }

    @Override
    public int getItemViewType(int position) {
        return servicos.get(position) == null ? VIEW_TYPE_LOADING: VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.layout_appointment_list, parent, false);
            return new ItemViewHolder(view);
        }else if(viewType == VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        return null;
    }

    public void setLoading() {
        isLoading = false;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            Servico servico = servicos.get(position);
            String estado = servicos.get(position).getEstado().toString();
            String id = servicos.get(position).getId();
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            //viewHolder.estado.setText(servicos.get(position).getEstado().toString());
            viewHolder.tecnico.setText(servicos.get(position).getTecnico());
            viewHolder.data.setText(servicos.get(position).getData());
            viewHolder.descricao.setText(servicos.get(position).getDescricao());
            viewHolder.morada.setText(servicos.get(position).getMorada());

            viewHolder.btn_reallocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.dialog_reallocate);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    NumberPicker numberPicker = dialog.findViewById(R.id.picker_tecnicos);
                    Button confirmBtn = dialog.findViewById(R.id.reallocate_btn_dialog);



                    Utils.getAllTecnicosNome(new SimpleCallback() {
                        @Override
                        public void callback(Object data) {
                            List<String> newUsers = new ArrayList<>();

                            for (String user : (List<String>) data){
                                if(!user.equals(servicos.get(position).getTecnico())){
                                    newUsers.add(user);
                                }
                            }

                            confirmBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String value = newUsers.get(numberPicker.getValue());
                                    Utils.serviceRef.child(id).child("tecnico").setValue(value);
                                    dialog.cancel();
                                }
                            });

                            numberPicker.setMinValue(0);
                            numberPicker.setMaxValue(newUsers.size() - 1);

                            String[] arr = newUsers.toArray(new String[0]);

                            numberPicker.setDisplayedValues(arr);

                            dialog.show();
                        }
                    });



                }
            });

            viewHolder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Carregou em Cancelar", Toast.LENGTH_SHORT).show();
                    viewHolder.btn_cancel.setVisibility(View.INVISIBLE);
                    viewHolder.btn_reallocate.setVisibility(View.INVISIBLE);
                    Utils.serviceRef.child(id).child("estado").setValue(Estado.Cancelado);
                }
            });

            switch (estado){
                case "Pendente":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_pendent_accepted));
                    viewHolder.btn_reallocate.setVisibility(View.VISIBLE);
                    viewHolder.btn_cancel.setVisibility(View.VISIBLE);
                    break;
                case "Cancelado":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_cancel_service)); break;
                case "Concluido":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_conclued_service)); break;
                case "Rejeitado":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_rejected_service)); break;
                case "Pendente_por_aceitar":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_pendent_not_accepted));
                    viewHolder.btn_reallocate.setVisibility(View.VISIBLE);
                    viewHolder.btn_cancel.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }else if(holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);

        }
    }

    @Override
    public int getItemCount() {
        return servicos.size();
    }
}
