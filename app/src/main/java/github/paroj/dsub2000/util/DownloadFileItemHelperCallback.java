package github.paroj.dsub2000.util;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;


import java.util.Queue;
import java.util.ArrayDeque;

import github.paroj.dsub2000.adapter.SectionAdapter;
import github.paroj.dsub2000.fragments.SubsonicFragment;
import github.paroj.dsub2000.service.DownloadFile;
import github.paroj.dsub2000.service.DownloadService;
import github.paroj.dsub2000.view.SongView;
import github.paroj.dsub2000.view.UpdateView;

public class DownloadFileItemHelperCallback extends ItemTouchHelper.SimpleCallback {
	private static final String TAG = DownloadFileItemHelperCallback.class.getSimpleName();

	private SubsonicFragment fragment;
	private boolean mainList;

	private BackgroundTask pendingTask = null;
	private Queue pendingOperations = new ArrayDeque();

	public DownloadFileItemHelperCallback(SubsonicFragment fragment, boolean mainList) {
		super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		this.fragment = fragment;
		this.mainList = mainList;
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder fromHolder, RecyclerView.ViewHolder toHolder) {
		int from = fromHolder.getAdapterPosition();
		int to = toHolder.getAdapterPosition();
		getSectionAdapter().moveItem(from, to);

		synchronized (pendingOperations) {
			pendingOperations.add(new Pair<>(from, to));
			updateDownloadService();
		}
		return true;
	}

	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		SongView songView = (SongView) ((UpdateView.UpdateViewHolder) viewHolder).getUpdateView();
		DownloadFile downloadFile = songView.getDownloadFile();

		getSectionAdapter().removeItem(downloadFile);
		synchronized (pendingOperations) {
			pendingOperations.add(downloadFile);
			updateDownloadService();
		}
	}

	public DownloadService getDownloadService() {
		return fragment.getDownloadService();
	}
	public SectionAdapter getSectionAdapter() {
		return fragment.getCurrentAdapter();
	}

	private void updateDownloadService() {
		if(pendingTask == null) {
			final DownloadService downloadService = getDownloadService();
			if(downloadService == null) {
				return;
			}

			pendingTask = new SilentBackgroundTask<Void>(downloadService) {
				@Override
				protected Void doInBackground() throws Throwable {
					boolean running = true;
					while(running) {
						Object nextOperation = null;
						synchronized (pendingOperations) {
							if(!pendingOperations.isEmpty()) {
								nextOperation = pendingOperations.remove();
							}
						}

						if(nextOperation != null) {
							if(nextOperation instanceof Pair) {
								Pair<Integer, Integer> swap = (Pair) nextOperation;
								downloadService.swap(mainList, swap.getFirst(), swap.getSecond());
							} else if(nextOperation instanceof DownloadFile) {
								DownloadFile downloadFile = (DownloadFile) nextOperation;
								if(mainList) {
									downloadService.remove(downloadFile);
								} else {
									downloadService.removeBackground(downloadFile);
								}
							}
						} else {
							running = false;
						}
					}

					synchronized (pendingOperations) {
						pendingTask = null;

						// Start a task if this is non-empty.  Means someone added while we were running operations
						if(!pendingOperations.isEmpty()) {
							updateDownloadService();
						}
					}
					return null;
				}
			};
			pendingTask.execute();
		}
	}
}
