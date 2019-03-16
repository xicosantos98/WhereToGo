package ipvc.estg.wheretogo.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ipvc.estg.wheretogo.Classes.ILoadMore;
import ipvc.estg.wheretogo.Classes.Servico;
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

    public ItemViewHolderTec (View itemView){
        super(itemView);
        morada = itemView.findViewById(R.id.service_card_address_tec);
        data = itemView.findViewById(R.id.service_card_date_tec);
        descricao = itemView.findViewById(R.id.service_card_description_tec);
        estado = itemView.findViewById(R.id.service_card_status_tec);
        color_estado = itemView.findViewById(R.id.estado_color_tec);
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
            Servico servico = servicos.get(position);
            String estado = servicos.get(position).getEstado().toString();
            ItemViewHolderTec viewHolder = (ItemViewHolderTec) holder;
            viewHolder.estado.setText(servicos.get(position).getEstado().toString());
            viewHolder.data.setText(servicos.get(position).getData()+" "+ servicos.get(position).getHoraPrevista());
            viewHolder.descricao.setText(servicos.get(position).getDescricao());
            viewHolder.morada.setText(servicos.get(position).getMorada());

            switch (estado){
                case "Pendente":
                    viewHolder.color_estado.setColorFilter(a.getColor(R.color.orange)); break;
                case "Cancelado":
                    viewHolder.color_estado.setColorFilter(a.getColor(R.color.red)); break;
                case "Concluido":
                    viewHolder.color_estado.setColorFilter(a.getColor(R.color.green)); break;
                case "Rejeitado":
                    viewHolder.color_estado.setColorFilter(a.getColor(R.color.grey)); break;
                default:
                    viewHolder.color_estado.setColorFilter(a.getColor(R.color.colorPrimary)); break;
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
