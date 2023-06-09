
package com.example.appinformationlist
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private val PERMISSIONS_REQUEST_READ_APPS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppList()
            // パーミッションのリクエスト
            if (!hasReadAppsPermission()) {
                requestReadAppsPermission()
            } else {
                AppList()
            }
        }
    }

    private fun hasReadAppsPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadAppsPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSIONS_REQUEST_READ_APPS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_READ_APPS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setContent {
                    AppList()
                }
            } else {
                // パーミッションが拒否された場合の処理を追加することができます
            }
        }
    }
}

@Composable
fun AppList() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    LazyColumn {
        items(apps) { appInfo ->
            appInfo.className?.let { className ->
                AppItem(appInfo.packageName, className, appInfo.loadLabel(packageManager).toString())
                Spacer(modifier = Modifier.height(16.dp))
            } ?: AppItem(appInfo.packageName, "undefined", appInfo.loadLabel(packageManager).toString())
            Spacer(modifier = Modifier.height(16.dp))
        }
    }}

@Composable
fun AppItem(packageName: String, className: String, appName: String) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = appName, style = MaterialTheme.typography.h6)
            Text(text = "Package Name: $packageName", style = MaterialTheme.typography.body2)
            Text(text = "Class Name: $className", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(
                    onClick = {
                        val clipboardData = ClipData.newPlainText("Package Name", packageName)
                        clipboardManager.setPrimaryClip(clipboardData)
                        Toast.makeText(context, "Package Name copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Copy Package Name")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val clipboardData = ClipData.newPlainText("Class Name", className)
                        clipboardManager.setPrimaryClip(clipboardData)
                        Toast.makeText(context, "Class Name copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Copy Class Name")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        context.startActivity(intent)
                    }
                }
            ) {
                Text(text = "Open App")
            }
        }    }}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        AppList()
    }
}