(ns playground.git)

(defn git [name]
  (let [master-branch {:name "master"
                       :commit nil}]
    {:name name
     :head master-branch
     :branches [master-branch]
     :last-commit-id -1}))

(defn add-commit-to-head-branch [repo commit]
  (let
    [index-of-branch (.indexOf (map :name (:branches repo)) (:name (:head repo)))
     old-branch (get (:branches repo) index-of-branch)
     new-branch (assoc old-branch :commit commit)
     modified-branches (assoc (:branches repo) index-of-branch new-branch)]
    (assoc repo :branches modified-branches)))

(defn commit [repo message]
  (let [commit-id (inc (:last-commit-id repo))
        this-commit {:id commit-id
                     :message message
                     :parent (:commit (:head repo))}
        head (assoc (:head repo) :commit this-commit)
        repo-with-modified-head (assoc repo :last-commit-id commit-id
                                            :head head)]
    (add-commit-to-head-branch repo-with-modified-head this-commit)))

(defn inner-log [a-commit]
  (let [parent (:parent a-commit)]
    (if parent
      (conj (inner-log parent) a-commit)
      (list a-commit))))

(defn log [repo] (inner-log (:commit (:head repo))))

(defn checkout [repo branch-name]
  (let
    [index-of-branch (.indexOf (map :name (repo :branches)) branch-name)
     branch-exists (not= index-of-branch -1)]
    (if branch-exists
      (assoc repo :head (get (:branches repo) index-of-branch))
      (let [new-branch {:name branch-name
                        :commit (:commit (:head repo))}]
        (assoc repo :head new-branch
                    :branches (conj (:branches repo) new-branch))))))

;;;; Poor-man's testing

;; Commit tests
(def repo (git "test"))
(def repo (commit repo "Inital commit"))
(def repo (commit repo "Change 1"))

(= (count (log repo)) 2) ;; Should have two commits
(= (:id (first (log repo))) 1) ;; Commit 1 should be first
(= (:id (second (log repo))) 0) ;; Commit 0 should be second

;; Checkout tests
(def repo (git "test"))
(def repo (commit repo "Initial commit"))
(= (:name (:head repo)) "master") ;; Should be on master branch

(def repo (checkout repo "testing"))
(= (:name (:head repo)) "testing") ;; Should be on testing branch

(def repo (checkout repo "master"))
(= (:name (:head repo)) "master") ;; Should be on master branch

(def repo (checkout repo "testing"))
(= (:name (:head repo)) "testing") ;; Should be on testing branch again.

;; Branch tests
(def repo (git "test"))
(def repo (commit repo "Initial commit"))
(def repo (commit repo "Change 1"))

(defn log-to-id-history [log]
  (clojure.string/join "-" (map :id log)))

(= (log-to-id-history (log repo)) "1-0") ;; Should show 2 commits

(def repo (checkout repo "testing"))
(def repo (commit repo "Change 2"))
(= (log-to-id-history (log repo)) "2-1-0") ;; Should show 3 commits

(def repo (checkout repo "master"))

(log-to-id-history (log repo))
repo

(= (log-to-id-history (log repo)) "1-0") ;; Should show 2 commits

(def repo (commit repo "Change 3"))
(= (log-to-id-history (log repo)) "3-1-0") ;; Should show 3 commits