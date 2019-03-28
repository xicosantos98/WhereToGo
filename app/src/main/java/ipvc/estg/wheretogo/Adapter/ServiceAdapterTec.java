package ipvc.estg.wheretogo.Adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ipvc.estg.wheretogo.Classes.Estado;
import ipvc.estg.wheretogo.Classes.ILoadMore;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.R;


class LoadingViewHolderTec extends RecyclerView.ViewHolder{
    public ProgressBar progressBar;

    public LoadingViewHolderTec(View itemView){
        super(itemView);
        progressBar = itemView.findViewById(R.id.progress_bar_items_tec);
    }
}

class ItemViewHolderTec extends RecyclerView.ViewHolder{
    public TextView morada, data, descricao, tecnico, estado;
    public ImageView color_estado;
    public Button b_accept, b_refuse, b_done, b_cancel;

    public ItemViewHolderTec (View itemView){
        super(itemView);
        morada = itemView.findViewById(R.id.service_card_address_tec);
        data = itemView.findViewById(R.id.service_card_date_tec);
        descricao = itemView.findViewById(R.id.service_card_description_tec);
        estado = itemView.findViewById(R.id.service_card_status_tec);
        color_estado = itemView.findViewById(R.id.estado_color_tec);
        b_accept = itemView.findViewById(R.id.service_card_accept_tec);
        b_refuse = itemView.findViewById(R.id.service_card_refuse_tec);
        b_done = itemView.findViewById(R.id.service_card_done_tec);
        b_cancel = itemView.findViewById(R.id.service_card_cancel_tec);
    }
}

public class ServiceAdapterTec extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<Servico> servicos;
    int visibleThreshold=20;
    int lastVisibleServico, totalServicoCount;

    Activity a;

    public ServiceAdapterTec(RecyclerView recyclerView, final Activity activity, List<Servico> servicos) {
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
                    .inflate(R.layout.layout_appointment_list_tec, parent, false);
            return new ItemViewHolderTec(view);
        }else if(viewType == VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.item_loading_tec, parent, false);
            return new LoadingViewHolderTec(view);
        }

        return null;
    }

    public void setLoading() {
        isLoading = false;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolderTec){
            Resources res  = holder.itemView.getContext().getResources();
            String id = servicos.get(position).getId();
            Servico servico = servicos.get(position);
            String estado = servicos.get(position).getEstado().toString();
            ItemViewHolderTec viewHolder = (ItemViewHolderTec) holder;
            viewHolder.data.setText(servicos.get(position).getData());
            viewHolder.descricao.setText(servicos.get(position).getDescricao());
            viewHolder.morada.setText(servicos.get(position).getMorada());

            viewHolder.b_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Carregou em Aceitar", Toast.LENGTH_SHORT).show();
                    viewHolder.b_accept.setVisibility(View.INVISIBLE);
                    viewHolder.b_refuse.setVisibility(View.INVISIBLE);
                    viewHolder.b_done.setVisibility(View.VISIBLE);
                    viewHolder.b_cancel.setVisibility(View.VISIBLE);
                    Utils.serviceRef.child(id).child("estado").setValue(Estado.Pendente);
                    NotificationManager manager = (NotificationManager) v.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancelAll();
                                    }
            });

            viewHolder.b_refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Carregou em Recusar", Toast.LENGTH_SHORT).show();
                    viewHolder.b_accept.setVisibility(View.INVISIBLE);
                    viewHolder.b_refuse.setVisibility(View.INVISIBLE);
                    viewHolder.b_done.setVisibility(View.VISIBLE);
                    viewHolder.b_cancel.setVisibility(View.VISIBLE);
                    Utils.serviceRef.child(id).child("estado").setValue(Estado.Rejeitado);

                }
            });

            viewHolder.b_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Carregou em Concluido", Toast.LENGTH_SHORT).show();
                    viewHolder.b_accept.setVisibility(View.INVISIBLE);
                    viewHolder.b_refuse.setVisibility(View.INVISIBLE);
                    viewHolder.b_done.setVisibility(View.INVISIBLE);
                    viewHolder.b_cancel.setVisibility(View.INVISIBLE);
                    Utils.serviceRef.child(id).child("estado").setValue(Estado.Concluido);

                }
            });

            viewHolder.b_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), "Carregou em Cancelar", Toast.LENGTH_SHORT).show();
                    viewHolder.b_accept.setVisibility(View.INVISIBLE);
                    viewHolder.b_refuse.setVisibility(View.INVISIBLE);
                    viewHolder.b_done.setVisibility(View.INVISIBLE);
                    viewHolder.b_cancel.setVisibility(View.INVISIBLE);
                    Utils.serviceRef.child(id).child("estado").setValue(Estado.Cancelado);
                }
            });

            switch (estado){
                case "Pendente":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_pendent_accepted));
                    viewHolder.estado.setText(res.getString(R.string.str_pending));
                    viewHolder.b_cancel.setVisibility(View.VISIBLE);
                    viewHolder.b_done.setVisibility(View.VISIBLE);
                    break;
                case "Cancelado":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_cancel_service));
                    viewHolder.estado.setText(res.getString(R.string.str_canceled));
                    break;
                case "Concluido":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_conclued_service));
                    viewHolder.estado.setText(res.getString(R.string.str_done));
                    break;
                case "Rejeitado":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_rejected_service));
                    viewHolder.estado.setText(res.getString(R.string.str_rejected));
                    break;
                case "Pendente_por_aceitar":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_pendent_not_accepted));
                    viewHolder.estado.setText(res.getString(R.string.str_pending_2));
                    viewHolder.b_accept.setVisibility(View.VISIBLE);
                    viewHolder.b_refuse.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }else if(holder instanceof LoadingViewHolderTec){
            LoadingViewHolderTec loadingViewHolder = (LoadingViewHolderTec) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);

        }
    }

    @Override
    public int getItemCount() {
        return servicos.size();
    }
}
