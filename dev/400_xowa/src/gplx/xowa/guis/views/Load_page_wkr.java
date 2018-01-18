/*
XOWA: the XOWA Offline Wiki Application
Copyright (C) 2012-2017 gnosygnu@gmail.com

XOWA is licensed under the terms of the General Public License (GPL) Version 3,
or alternatively under the terms of the Apache License Version 2.0.

You may use XOWA according to either of these licenses as is most appropriate
for your project on a case-by-case basis.

The terms of each license can be found in the source code repository:

GPLv3 License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-GPLv3.txt
Apache License: https://github.com/gnosygnu/xowa/blob/master/LICENSE-APACHE2.txt
*/
package gplx.xowa.guis.views; import cn.edu.ruc.xowa.log.domain.Page;
import cn.edu.ruc.xowa.log.domain.PageCache;
import cn.edu.ruc.xowa.log.domain.PageType;
import cn.edu.ruc.xowa.log.domain.Url;
import gplx.Gfo_invk_;
import gplx.core.threads.Gfo_thread_wkr;
import gplx.xowa.Xoa_ttl;
import gplx.xowa.Xoa_url;
import gplx.xowa.Xoae_page;
import gplx.xowa.Xowe_wiki;
public class Load_page_wkr implements Gfo_thread_wkr {
	private final    Xog_tab_itm tab;
	public Load_page_wkr(Xog_tab_itm tab, Xowe_wiki wiki, Xoa_url url, Xoa_ttl ttl) {this.tab = tab; this.wiki = wiki; this.url = url; this.ttl = ttl; }
	public String				Thread__name()		{return "xowa.load_page_wkr";}
	public boolean				Thread__resume()	{return false;}
	public Xowe_wiki			Wiki()				{return wiki;}			private final    Xowe_wiki wiki;
	public Xoa_url				Url()				{return url;}			private final    Xoa_url url;
	public Xoa_ttl				Ttl()				{return ttl;}			private final    Xoa_ttl ttl;
	public Xoae_page			Page()				{return page;}			private Xoae_page page;
	public Exception		Exec_err()			{return exec_err;}		private Exception exec_err;
	public void Thread__exec() {

		System.out.println();
		System.out.println("Load_page_wkr.Thread__exec()");
		System.out.println("load page: " + this.url.To_str());
		Url url1 = new Url(this.url.To_str());
		Page page1 = new Page(url1, PageType.TAB_PAGE);
		PageCache.getInstance().putPage(url1, page1);

		try {
			Running_(true);

			// wait_for_popups; free mem check;
			this.page = wiki.Page_mgr().Load_page(url, ttl, tab);
			// try to print html of the page in a tab
			//System.out.println(new String(page.Html_data().));

			// launch thread to show page
			Gfo_invk_.Invk_by_val(tab.Cmd_sync(), Xog_tab_itm.Invk_show_url_loaded_swt, this);
		}
		catch (Exception e) {
			this.exec_err = e;
			Gfo_invk_.Invk_by_val(tab.Cmd_sync(), Xog_tab_itm.Invk_show_url_failed_swt, this);
		}
		finally {
			Running_(false);
		}
	}
	private static final    Object thread_lock = new Object(); private static boolean running = false;
	public static boolean Running() {
		boolean rv = false;
		synchronized (thread_lock) {
			rv = running;
		}
		return rv;
	}
	private static void Running_(boolean v) {
		synchronized (thread_lock) {
			running = v;
		}
	}
}
