package hkapps.playmxtv.Adapters;

/**
 * Created by hkfuertes on 19/05/2017.
 */

public class Listener {
}
/*
private final class ItemViewClickedListener implements OnItemViewClickedListener {
    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {

        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Log.d(TAG, "Item: " + item.toString());
            Intent intent = new Intent(getActivity(), PeliculasDetailsActivity.class);
            intent.putExtra(PeliculasDetailsActivity.MOVIE, movie);

            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                    PeliculasDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
            getActivity().startActivity(intent, bundle);
        } else if (item instanceof String) {
            if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}

private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            mBackgroundURI = ((Movie) item).getBackgroundImageURI();
            startBackgroundTimer();
        }

    }
}
*/