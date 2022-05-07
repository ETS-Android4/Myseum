package com.deteksi.benda.tradisional.sumbawa

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import om.deteksi.benda.tradisional.sumbawa.R
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CaptureActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "TFLite - ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val MAX_FONT_SIZE = 96F
    }

    private lateinit var captureImageFab: AppCompatButton
    private lateinit var lihatSejarah: AppCompatButton
    private lateinit var inputImageView: ImageView
    private lateinit var tvPlaceHolder: TextView
    private lateinit var currentPhotoPath: String
    private lateinit var tvResult: AppCompatTextView

    private val listLabels = listOf(
        "badong",
        "batu parujak",
        "cilo bulaeng",
        "kalaru",
        "kendi",
        "kris kamutar sai",
        "pabekas salaka",
        "pabua galang",
        "pajula",
        "peti",
        "salepa"
    )

    private val listDesk = listOf(
        "Badong merupakan salah satu Regalia Kesultanan Sumbawa yang dijadikan sebagai simbol Kepatuhan  Dan Pengayoman. Badong terbuat dari Kayu Pilihan berbentuk Lingkaran dg Diameter 60 Cm. Cembung bagian luar dan Cekung bagian dalam. Diberi Pegangan kiri kanan pada Bagian Dalam. Ketebalan Kayu Badong sekitar 3 Cm dengan Pucuk berbentuk Bunga Bersusun Berkelopak 8 terbuat dari Emas ( Bulaeng ) dengan pucuk bagian atas diberi permata. Pada Tokal Adat ( Upacara Istana ) baik Tokal Adat Ode maupun Tokal Adat Rea...maka Badong dijunjung di atas kepala seorang Pria petugas khusus. Dari Awal Acara hingga Akhir Acara.",
        "Batu parujak merupakan seperangkat alat menumbuk bahan masakan dan obat-obatan yang digunakan oleh masyarakat Sumbawa tempo dulu. Batu parujak terbuat dari batu alam yang di bentuk serupa alu dan lesung . alat menumbuk ini masih tersimpan rapi dimuseum Sumbawa.",
        "Cilo bulaeng atau cilo kamutar adalah mahkota sultan Sumbawa yang terbuat dari emas, digunakan saat upacara tokal adat rea, dan penobatan mudzakara rea sebagai kelengkapan pangkenang kanadi “pasangengang” atau pakaian kebesaran.  Pada bagian pinggir terukir lafaz Allah dan Muhammad yang menjadi penanda bahwa mahkota ini bercorak islami.",
        "Kalaru tata yakni gelang yang menjadi kelengkapan pakaian pengantin wanita khas Sumbawa. Kalaru tata terbuat dari emas muda dan memiliki motif yang sangat khas dari daerah Sumbawa yakni kemang satange, lonto engal.",
        "Fungsi utama kendi adalah sebagai wadah minum di mana air tetap dingin sepanjang hari karena porositas tanah liat. Airnya dituangkan dari kendi langsung ke mulut Konon kendi yang berasal dari India meluas ke Asia Tengggara dan Cina dibawa pedagang dan pemuka agama. Di abad ke-14, kundika yang menjadi wadah ritual Hindu dan Budha di Indonesia mulai ditinggalkan. Namun istilah kendi sudah diserap dalam bahasa Melayu yakni kendi atau kundi. Sementara orang Jawa menyebut gendi.",
        "“Keris kamutar sai” merupakan salah satu “parewa kamutar” kesultanan Sumbawa yang berbentuk sebilah keris yang berbahan besi  unggulan dengan hulu dari emas dan bertahtakan berlian. Keris ini menjadi simbol keagunan dan kemuliaan seorang sultan dan sebagai symbol marwah Tau Tana Samawa.Dalam kedudukannya sebagai parewa kamutar, keris kamutar sai adalah regalia dan plural tantum sekaligus sebagai simbol hak dan sifat yang menjadi lambang kekuasaan seorang Sultan.Pada saat tokal adat rea berlangsung keris kamutar sai akan dipangku atau dibawa oleh seorang “tame” (petugas pembawa keris kamutar sai)",
        "Pakebas salaka yakni sebuah kipas yang terbuat dari perak yang berukir khas motif Sumbawa. Fungsinya adalah sebagai alat untuk mengipasi Sultan dan Permaisuri dalam acara “tokal adat ode” (acara adat).",
        "Pabua gelang yakni seperangkat tempat sirih pinang yang terbuat dari “suasa” / emas muda. Pabua gelang biasanya dipakai oleh masyarakat Sumbawa yang gemar “mama”/kebiasaan nyirih pada masa lalu.",
        "Pajula merupakan wadah yang difungsikan sebagai tempat meludah kaum bangsawan Sumbawa. Pajula pada umumnya terbuat dari logam mulia seperti emas, perak, perunggu dan kuningan. Pajula memiliki tinggi 25 cm dan diameter 8 cm.",
        "Kandaga merupakan peti kuno yang terbuat dari kayu yang sangat keras. Bagian bidang luarnya diukir dengan ragam motif khas Sumbawa seperti lonto engal, wafak, kemang satange dan lain-lain. Bagian tutupnya ditatah dengan kulit lokan mutiara. Kandaga berfungsi sebagai peti tempat menyimpan barang berhagara seperti emas, kre alang, perhiasan dll.",
        "Salepa bulaeng merupakan kelengkapan piranti adat “tokal adat ode” kesultan Sumbawa. Salepa bulaeng ini merupakan simbolisasi kebesaran kesultanan Sumbawa yang berbentuk kotak dan  terbuat dari emas serta dipenuhi dengan ornamen ukiran lonto engal khas Sumbawa. Bagian dalamnya terbuat dari salaka “perak”.  Salepa bulaeng berfungsi sebagai wadah rokok sultan."
    )

    var labels = ""
    var desk = ""
    var img = 0

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                runCamera()
            } else {
                Toast.makeText(
                    this,
                    "Izin di tolak silah restart ulang aplikasi untuk menggunakan fitur ini",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        captureImageFab = findViewById(R.id.captureImageFab)
        lihatSejarah = findViewById(R.id.lihatSejarah)
        inputImageView = findViewById(R.id.imageView)
        tvPlaceHolder = findViewById(R.id.placeholder)
        tvResult = findViewById(R.id.tvResult)

        captureImageFab.setOnClickListener(this)
        lihatSejarah.setOnClickListener(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
            resultCode == Activity.RESULT_OK
        ) {
            setViewAndDetect(getCapturedImage())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.captureImageFab -> {
                permission()
            }
            R.id.lihatSejarah -> {
                val builder = AlertDialog.Builder(this)
                builder.setView(viewDialog(this))
                builder.setTitle("Info sejarah")
                builder.setPositiveButton("Kembali") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    private fun permission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                runCamera()
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun runCamera() {
        try {
            dispatchTakePictureIntent()
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, e.message.toString())
        }
    }

    /**
     * runObjectDetection(bitmap: Bitmap)
     *      TFLite Object Detection function
     */
    private fun runObjectDetection(bitmap: Bitmap) {
        // Step 1: Create TFLite's TensorImage object
        val image = TensorImage.fromBitmap(bitmap)

        // Step 2: Initialize the detector object
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(5)
            .setScoreThreshold(0.3f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "model_new.tflite",
            options
        )

        // Step 3: Feed given image to the detector
        val results = detector.detect(image)

        Log.println(Log.ASSERT, "runObjectDetection", results.toString())

        var text = ""
        var isDeteksi = false
        // Step 4: Parse the detection result and show it
        val resultToDisplay = results.map {
            // Get the top-1 category and craft the display text
            val category = it.categories.first()
            text = "${category.label}, ${category.score.times(100).toInt()}%"
            labels = category.label

            Log.println(Log.ASSERT, "labels", labels)

            if (category.score.times(100).toInt() > 70 && labels.isNotEmpty()) {
                isDeteksi = true
                when (labels) {
                    listLabels[0] -> {
                        img = R.drawable.img_badong
                        desk = listDesk[0]
                    }
                    listLabels[1] -> {
                        img = R.drawable.img_batu_parujak
                        desk = listDesk[1]
                    }
//                    listLabels[2] -> {
//                        img = R.drawable.img_bokar
//                        desk = listDesk[2]
//                    }
                    listLabels[2] -> {
                        img = R.drawable.img_cilo_bulaeng
                        desk = listDesk[2]
                    }
                    listLabels[3] -> {
                        img = R.drawable.img_kalaru
                        desk = listDesk[3]
                    }
                    listLabels[4] -> {
                        img = R.drawable.img_kendi
                        desk = listDesk[4]
                    }
                    listLabels[5] -> {
                        img = R.drawable.img_kris_kamutar_sai
                        desk = listDesk[5]
                    }
                    listLabels[6] -> {
                        img = R.drawable.img_pabekas_salaka
                        desk = listDesk[6]
                    }
                    listLabels[7] -> {
                        img = R.drawable.img_pabua_gelang
                        desk = listDesk[7]
                    }
                    listLabels[8] -> {
                        img = R.drawable.img_pajula
                        desk = listDesk[8]
                    }
                    listLabels[9] -> {
                        img = R.drawable.img_peti
                        desk = listDesk[9]
                    }
                    listLabels[10] -> {
                        img = R.drawable.img_salepa
                        desk = listDesk[10]
                    }
                }
            } else {
                isDeteksi = false
//                Toast.makeText(this, "Gambar tidak terdeteksi", Toast.LENGTH_LONG)
            }

            // Create a data object to display the detection result
            DetectionResult(it.boundingBox, text)
        }
        // Draw the detection result on the bitmap and show it.
        val imgWithResult = drawDetectionResult(bitmap, resultToDisplay)
        runOnUiThread {
            if (isDeteksi) {
                inputImageView.visibility = View.VISIBLE
                inputImageView.setImageBitmap(imgWithResult)
                lihatSejarah.isEnabled = true
                tvResult.text = text
                lihatSejarah.isEnabled = true
            } else {
                tvResult.visibility = View.GONE
                inputImageView.visibility = View.GONE
                tvPlaceHolder.visibility = View.VISIBLE
                lihatSejarah.isEnabled = false
                tvPlaceHolder.text = "Gambar tidak terdeteksi"
            }
        }
    }

    /**
     * debugPrint(visionObjects: List<Detection>)
     *      Print the detection result to logcat to examine
     */
    private fun debugPrint(results: List<Detection>) {
        for ((i, obj) in results.withIndex()) {
            val box = obj.boundingBox

            Log.d(TAG, "Detected object: ${i} ")
            Log.d(TAG, "  boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")

            for ((j, category) in obj.categories.withIndex()) {
                Log.d(TAG, "    Label $j: ${category.label}")
                val confidence: Int = category.score.times(100).toInt()
                Log.d(TAG, "    Confidence: ${confidence}%")
            }
        }
    }

    /**
     * setViewAndDetect(bitmap: Bitmap)
     *      Set image to view and call object detection
     */
    private fun setViewAndDetect(bitmap: Bitmap) {
        // Display capture image
        inputImageView.setImageBitmap(bitmap)
        tvPlaceHolder.visibility = View.INVISIBLE
        tvResult.visibility = View.VISIBLE

        // Run ODT and display result
        // Note that we run this in the background thread to avoid blocking the app UI because
        // TFLite object detection is a synchronised process.
        lifecycleScope.launch(Dispatchers.Default) { runObjectDetection(bitmap) }
    }

    /**
     * getCapturedImage():
     *      Decodes and crops the captured image from camera.
     */
    private fun getCapturedImage(): Bitmap {
        // Get the dimensions of the View
        val targetW: Int = inputImageView.width
        val targetH: Int = inputImageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        val exifInterface = ExifInterface(currentPhotoPath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(bitmap, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(bitmap, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(bitmap, 270f)
            }
            else -> {
                bitmap
            }
        }
    }

    /**
     * getSampleImage():
     *      Get image form drawable and convert to bitmap.
     */
    private fun getSampleImage(drawable: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, drawable, BitmapFactory.Options().apply {
            inMutable = true
        })
    }

    /**
     * rotateImage():
     *     Decodes and crops the captured image from camera.
     */
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    /**
     * createImageFile():
     *     Generates a temporary image file for the Camera app to write to.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    /**
     * dispatchTakePictureIntent():
     *     Start the Camera app to take a photo.
     */
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.deteksi.benda.tradisional.sumbawa.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    /**
     * drawDetectionResult(bitmap: Bitmap, detectionResults: List<DetectionResult>
     *      Draw a box around each objects and show the object's name.
     */
    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<DetectionResult>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)


            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )
        }
        return outputBitmap
    }

    private fun viewDialog(context: Context): View? {

        val layout = LayoutInflater.from(context)
            .inflate(R.layout.custom_dialog, null, false)
        val imgView = layout.findViewById<AppCompatImageView>(R.id.img_view)
        val tvLabels = layout.findViewById<AppCompatTextView>(R.id.tv_labels)
        val tvDesk = layout.findViewById<AppCompatTextView>(R.id.tv_desk)

        imgView.setImageResource(img)
        tvLabels.text = labels
        tvDesk.text = desk

        tvDesk.movementMethod = ScrollingMovementMethod()

        return layout
    }

}

/**
 * DetectionResult
 *      A class to store the visualization info of a detected object.
 */
data class DetectionResult(val boundingBox: RectF, val text: String)