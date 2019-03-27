package ipvc.estg.wheretogo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ipvc.estg.wheretogo.Admin.AppointmentList;
import ipvc.estg.wheretogo.Classes.ILoadMore;
import ipvc.estg.wheretogo.Classes.Servico;
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

    public ItemViewHolder (View itemView){
        super(itemView);
        morada = itemView.findViewById(R.id.service_card_address);
        data = itemView.findViewById(R.id.service_card_date);
        descricao = itemView.findViewById(R.id.service_card_description);
        tecnico = itemView.findViewById(R.id.service_card_technician);
        //estado = itemView.findViewById(R.id.service_card_status);
        color_estado = itemView.findViewById(R.id.estado_color);
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
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            //viewHolder.estado.setText(servicos.get(position).getEstado().toString());
            viewHolder.tecnico.setText(servicos.get(position).getTecnico());
            viewHolder.data.setText(servicos.get(position).getData());
            viewHolder.descricao.setText(servicos.get(position).getDescricao());
            viewHolder.morada.setText(servicos.get(position).getMorada());

            switch (estado){
                case "Pendente":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_pendent_accepted)); break;
                case "Cancelado":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_cancel_service)); break;
                case "Concluido":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_conclued_service)); break;
                case "Rejeitado":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_rejected_service)); break;
                case "Pendente_por_aceitar":
                    viewHolder.color_estado.setImageDrawable(a.getDrawable(R.drawable.ic_pendent_not_accepted));break;
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
