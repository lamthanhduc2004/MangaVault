import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { followStory, unfollowStory, isFollowing } from '../services/libraryService';
import { useAuth } from '../context/AuthContext';

export default function FollowButton({ storyId }) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [following, setFollowing] = useState(false);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (!user) {
      setFollowing(false);
      return;
    }
    isFollowing(storyId).then(setFollowing).catch(() => setFollowing(false));
  }, [storyId, user]);

  const handleClick = async () => {
    if (!user) {
      navigate('/login', { state: { from: `/stories/${storyId}` } });
      return;
    }
    setBusy(true);
    try {
      if (following) {
        await unfollowStory(storyId);
        setFollowing(false);
      } else {
        await followStory(storyId);
        setFollowing(true);
      }
    } catch {
      // Most likely a duplicate/missing follow — refresh from the server.
      isFollowing(storyId).then(setFollowing).catch(() => {});
    } finally {
      setBusy(false);
    }
  };

  return (
    <button onClick={handleClick} disabled={busy} className={following ? 'btn-small' : 'btn-primary'}>
      {following ? '✓ Đang theo dõi' : '+ Theo dõi'}
    </button>
  );
}
