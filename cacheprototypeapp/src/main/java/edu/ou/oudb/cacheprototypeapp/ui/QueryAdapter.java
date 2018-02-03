package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.ui.ProcessedQueriesFragment.ViewHolder;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

public class QueryAdapter extends ArrayAdapter<Query> {


    public QueryAdapter(Context context, int textViewResourceId, List<Query> queries) {
        super(context, textViewResourceId, queries);
    }


    @Override
    public View getView(int position, View row, ViewGroup parent) {

        ProcessedQueriesFragment.ViewHolder holder;

        Query query = getItem(position);

        if (row == null) {
            // remplissage de la vue
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.queries_item, parent, false);

            holder = new ViewHolder();

            holder.queryRelation = (TextView) row.findViewById(R.id.relation);
            holder.predicates = (TextView) row.findViewById(R.id.predicates);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.queryRelation.setText(new StringBuilder("\u03C3")
                .append('(')
                .append(query.getRelation())
                .append(')'));

        StringBuilder sb = new StringBuilder();
        Set<Predicate> predicates = query.getPredicates();
        Set<Predicate> excludedPredicates = query.getExcludedPredicates();

        if (predicates.size() != 0) {
            Iterator<Predicate> it = predicates.iterator();

            sb.append(it.next());

            while (it.hasNext()) {
                sb.append(" AND ");
                sb.append(it.next());
            }
        }

        if (excludedPredicates.size() != 0) {
            Iterator<Predicate> it = excludedPredicates.iterator();

            sb.append(" AND NOT ( ");
            sb.append(it.next());

            while (it.hasNext()) {
                sb.append(" AND ");
                sb.append(it.next());
            }

            sb.append(")");
        }


        holder.predicates.setText(sb.toString().replace("%27", "'")
                .replace("%20", " "));


        return row;
    }


}
