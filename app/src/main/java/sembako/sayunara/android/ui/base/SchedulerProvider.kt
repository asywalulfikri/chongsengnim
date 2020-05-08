package sembako.sayunara.android.ui.base

import io.reactivex.Scheduler

interface SchedulerProvider{
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun io(): Scheduler
}