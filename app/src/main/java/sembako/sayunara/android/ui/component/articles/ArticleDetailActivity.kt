package sembako.sayunara.android.ui.component.articles

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import kotlinx.android.synthetic.main.activity_detail_articles.*
import kotlinx.android.synthetic.main.layout_progress_bar_with_text.*
import kotlinx.android.synthetic.main.toolbar.*
import sembako.sayunara.android.R
import sembako.sayunara.android.ui.base.BaseActivity


internal open class ArticleDetailActivity : BaseActivity() {


    var articles : Articles? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_articles)
        setupToolbar(toolbar)
        articles = intent.getSerializableExtra("articles") as Articles
        prepareView()
    }

    open fun prepareView() {
        initView()
    }


    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    protected fun initView() {

        val head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>"
        val custom = "<html>" +
                head +
                "<body style='color:#555555;font-size:14px'>" + articles?.html +
                "</body>" +
                "</html>"


        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                if (newProgress == 100)
                    layout_progress.visibility = View.GONE
                else
                    layout_progress.visibility = View.VISIBLE
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(
                    "Webview logs", consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId()
                )
                return super.onConsoleMessage(consoleMessage)
            }

        }


        webView.settings.javaScriptEnabled = true
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.settings.domStorageEnabled = true
        webView.isHorizontalScrollBarEnabled = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }
        else
        {
            webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }

        webView.loadDataWithBaseURL(null, custom, "text/html", "utf-8", null)
    }



}


