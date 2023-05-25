package app.k9mail.autodiscovery.autoconfig

import app.k9mail.core.common.net.Domain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible

class SuspendableMxResolver(private val mxResolver: MxResolver) {
    suspend fun lookup(domain: Domain): List<Domain> {
        return runInterruptible(Dispatchers.IO) {
            mxResolver.lookup(domain)
        }
    }
}